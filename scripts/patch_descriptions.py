#!/usr/bin/env python3
import argparse
import json
import os
import shutil
import sqlite3
import time
from typing import Dict, Any


def parse_args():
    p = argparse.ArgumentParser(
        description="Patch token descriptions in prebuilt_tokens.db safely. "
                    "Updates ONLY tokens.description by cgId. Optionally bumps PRAGMA user_version."
    )
    p.add_argument("--db", required=True, help="Path to sqlite db (e.g., app/src/main/assets/prebuilt_tokens.db)")
    p.add_argument("--in", dest="infile", required=True, help="Input JSON: {cgId: description, ...}")
    p.add_argument("--backup", action="store_true", help="Create timestamped backup of the DB before patching")
    p.add_argument("--overwrite", action="store_true", help="Overwrite descriptions even if already non-empty")
    p.add_argument("--bump-user-version", action="store_true",
                   help="After successful patch, increment PRAGMA user_version by 1 (recommended for auto-sync in app)")
    return p.parse_args()


def load_json(path: str) -> Dict[str, str]:
    with open(path, "r", encoding="utf-8") as f:
        data = json.load(f)
    if not isinstance(data, dict):
        raise ValueError("Input JSON must be an object: {cgId: description, ...}")
    # ensure values are strings
    out: Dict[str, str] = {}
    for k, v in data.items():
        if k is None:
            continue
        if v is None:
            continue
        out[str(k)] = str(v).strip()
    return out


def backup_db(db_path: str) -> str:
    ts = time.strftime("%Y%m%d-%H%M%S")
    bak_path = f"{db_path}.bak-{ts}"
    shutil.copy2(db_path, bak_path)
    return bak_path


def get_user_version(conn: sqlite3.Connection) -> int:
    cur = conn.execute("PRAGMA user_version;")
    row = cur.fetchone()
    return int(row[0]) if row and row[0] is not None else 0


def set_user_version(conn: sqlite3.Connection, new_version: int):
    conn.execute(f"PRAGMA user_version={int(new_version)};")


def main():
    args = parse_args()

    db_path = args.db
    if not os.path.exists(db_path):
        raise FileNotFoundError(f"DB not found: {db_path}")

    updates = load_json(args.infile)
    if not updates:
        print("[info] No updates in input JSON. Nothing to do.")
        return

    if args.backup:
        bak = backup_db(db_path)
        print(f"[ok] Backup created: {bak}")

    conn = sqlite3.connect(db_path)
    conn.execute("PRAGMA journal_mode=WAL;")
    conn.execute("PRAGMA synchronous=NORMAL;")

    before_uv = get_user_version(conn)

    # stats
    total_in_json = len(updates)
    matched = 0
    updated = 0
    skipped_already_has = 0

    # Use transaction for safety + speed
    try:
        conn.execute("BEGIN;")

        # Verify table exists
        cur = conn.execute("SELECT name FROM sqlite_master WHERE type='table' AND name='tokens';")
        if cur.fetchone() is None:
            raise RuntimeError("Table 'tokens' not found in DB. Are you pointing to the correct prebuilt DB?")

        # Prepare statement
        if args.overwrite:
            sql = "UPDATE tokens SET description=? WHERE cgId=?;"
        else:
            sql = "UPDATE tokens SET description=? WHERE cgId=? AND (description IS NULL OR description='');"

        for cg_id, desc in updates.items():
            if not cg_id or not desc:
                continue

            # Check if token exists (optional, for stats)
            exists = conn.execute("SELECT 1 FROM tokens WHERE cgId=? LIMIT 1;", (cg_id,)).fetchone()
            if not exists:
                continue

            matched += 1

            if not args.overwrite:
                # If already has description, it will be skipped by WHERE clause.
                # We'll detect if it had one to report stats.
                had = conn.execute(
                    "SELECT 1 FROM tokens WHERE cgId=? AND description IS NOT NULL AND description!='' LIMIT 1;",
                    (cg_id,)
                ).fetchone()
                if had:
                    skipped_already_has += 1
                    continue

            cur2 = conn.execute(sql, (desc, cg_id))
            if cur2.rowcount and cur2.rowcount > 0:
                updated += 1

        # bump user_version if requested AND we actually updated something
        if args.bump_user_version and updated > 0:
            after_uv = before_uv + 1
            set_user_version(conn, after_uv)
        else:
            after_uv = before_uv

        conn.execute("COMMIT;")

    except Exception as e:
        conn.execute("ROLLBACK;")
        conn.close()
        raise e

    # compute remaining missing count
    remaining_missing = conn.execute(
        "SELECT COUNT(*) FROM tokens WHERE description IS NULL OR description='';"
    ).fetchone()[0]

    final_uv = get_user_version(conn)
    conn.close()

    print(f"[ok] JSON items: {total_in_json}")
    print(f"[ok] matched cgId in DB: {matched}")
    if not args.overwrite:
        print(f"[ok] skipped (already had description): {skipped_already_has}")
    print(f"[ok] updated descriptions: {updated}")
    print(f"[ok] remaining missing: {remaining_missing}")

    if args.bump_user_version:
        print(f"[ok] user_version: {before_uv} -> {final_uv}")
    else:
        print(f"[info] user_version unchanged: {before_uv}")

    if updated == 0:
        print("[warn] No rows updated. Check cgIds, or use --overwrite if you intend to replace existing descriptions.")


if __name__ == "__main__":
    main()
