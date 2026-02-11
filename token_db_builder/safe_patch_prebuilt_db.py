#!/usr/bin/env python3
import argparse
import json
import os
import shutil
import sqlite3
import time
from typing import Dict, Any, List, Tuple, Optional

REQUIRED_NOT_NULL = {"cgId", "symbol", "name", "updatedAt"}

CREATE_TOKENS_SQL = """
CREATE TABLE IF NOT EXISTS tokens(
  cgId        TEXT NOT NULL PRIMARY KEY,
  symbol      TEXT NOT NULL,
  name        TEXT NOT NULL,
  description TEXT,
  imageUrl    TEXT,
  updatedAt   INTEGER NOT NULL
);
"""

IDX_SYMBOL = "CREATE INDEX IF NOT EXISTS idx_tokens_symbol ON tokens(symbol);"
IDX_NAME   = "CREATE INDEX IF NOT EXISTS idx_tokens_name   ON tokens(name);"

def now_ts() -> int:
    return int(time.time())

def connect_db(path: str) -> sqlite3.Connection:
    conn = sqlite3.connect(path)
    conn.execute("PRAGMA journal_mode=WAL;")
    conn.execute("PRAGMA synchronous=NORMAL;")
    conn.execute("PRAGMA busy_timeout=30000;")
    return conn

def table_info(conn: sqlite3.Connection, table: str) -> List[Tuple]:
    # cid, name, type, notnull, dflt_value, pk
    return conn.execute(f"PRAGMA table_info({table});").fetchall()

def has_tokens_table(conn: sqlite3.Connection) -> bool:
    row = conn.execute(
        "SELECT name FROM sqlite_master WHERE type='table' AND name='tokens';"
    ).fetchone()
    return row is not None

def current_user_version(conn: sqlite3.Connection) -> int:
    row = conn.execute("PRAGMA user_version;").fetchone()
    return int(row[0]) if row else 0

def set_user_version(conn: sqlite3.Connection, v: int):
    conn.execute(f"PRAGMA user_version={int(v)};")

def schema_is_room_compatible(conn: sqlite3.Connection) -> Tuple[bool, str]:
    if not has_tokens_table(conn):
        return (False, "tokens table missing")

    info = table_info(conn, "tokens")
    cols = {r[1]: r for r in info}  # name -> row tuple

    missing = [c for c in ["cgId","symbol","name","description","imageUrl","updatedAt"] if c not in cols]
    if missing:
        return (False, f"missing columns: {missing}")

    # NOT NULL checks for required fields
    bad_nn = []
    for c in REQUIRED_NOT_NULL:
        notnull = int(cols[c][3])  # 1 means NOT NULL
        if notnull != 1:
            bad_nn.append(c)

    # PK check: cgId pk==1
    pk = int(cols["cgId"][5])
    if pk != 1:
        return (False, f"cgId is not PRIMARY KEY (pk={pk})")

    if bad_nn:
        return (False, f"NOT NULL mismatch for: {bad_nn}")

    return (True, "ok")

def rebuild_tokens_table_room_schema(conn: sqlite3.Connection):
    """
    Rebuild tokens table with strict Room-compatible schema (NOT NULL, PK).
    Preserves data by copying with COALESCE defaults.
    """
    conn.execute("BEGIN;")

    # Create new table
    conn.execute("""
    CREATE TABLE IF NOT EXISTS tokens_new(
      cgId        TEXT NOT NULL PRIMARY KEY,
      symbol      TEXT NOT NULL,
      name        TEXT NOT NULL,
      description TEXT,
      imageUrl    TEXT,
      updatedAt   INTEGER NOT NULL
    );
    """)

    # Copy data safely; enforce non-null defaults
    conn.execute("""
    INSERT OR REPLACE INTO tokens_new (cgId, symbol, name, description, imageUrl, updatedAt)
    SELECT
      COALESCE(cgId, ''),
      COALESCE(symbol, ''),
      COALESCE(name, ''),
      description,
      imageUrl,
      COALESCE(updatedAt, 0)
    FROM tokens;
    """)

    # Drop old + rename
    conn.execute("DROP TABLE tokens;")
    conn.execute("ALTER TABLE tokens_new RENAME TO tokens;")

    # Indices
    conn.execute(IDX_SYMBOL)
    conn.execute(IDX_NAME)

    conn.execute("COMMIT;")

def ensure_schema(conn: sqlite3.Connection):
    if not has_tokens_table(conn):
        conn.execute(CREATE_TOKENS_SQL)
        conn.execute(IDX_SYMBOL)
        conn.execute(IDX_NAME)
        return

    ok, reason = schema_is_room_compatible(conn)
    if ok:
        return

    print(f"[schema] not compatible: {reason} -> rebuilding tokens table")
    rebuild_tokens_table_room_schema(conn)

def load_patch_file(path: str) -> Dict[str, Any]:
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)

def apply_patches(conn: sqlite3.Connection, patch: Dict[str, Any], dry_run: bool = False) -> Tuple[int,int,int]:
    """
    Patch format accepted:
    - { "items": [ { "cgId": "...", "description": "...", "imageUrl": "..."} ] }
    - or a dict: { "<cgId>": { "description": "...", "imageUrl": "..." }, ... }
    - or a list directly
    """
    items = None

    if isinstance(patch, dict) and "items" in patch and isinstance(patch["items"], list):
        items = patch["items"]
    elif isinstance(patch, list):
        items = patch
    elif isinstance(patch, dict):
        # map form
        tmp = []
        for k, v in patch.items():
            if isinstance(v, dict):
                tmp.append({"cgId": k, **v})
        items = tmp
    else:
        raise ValueError("Unknown patch json structure")

    updated = 0
    missing = 0
    skipped = 0

    cur = conn.cursor()
    ts = now_ts()

    for it in items:
        cgId = (it.get("cgId") or it.get("id") or "").strip()
        if not cgId:
            skipped += 1
            continue

        desc = it.get("description")
        img  = it.get("imageUrl")

        # Do we have this token?
        exists = cur.execute("SELECT 1 FROM tokens WHERE cgId=? LIMIT 1;", (cgId,)).fetchone()
        if not exists:
            missing += 1
            continue

        # Only patch if something present
        if (desc is None or desc == "") and (img is None or img == ""):
            skipped += 1
            continue

        if dry_run:
            updated += 1
            continue

        cur.execute(
            """
            UPDATE tokens
               SET description = COALESCE(?, description),
                   imageUrl    = COALESCE(?, imageUrl),
                   updatedAt   = ?
             WHERE cgId = ?;
            """,
            (desc if desc else None, img if img else None, ts, cgId)
        )
        updated += 1

    if not dry_run:
        conn.commit()

    return updated, missing, skipped

def main():
    ap = argparse.ArgumentParser(description="Safely patch prebuilt_tokens.db: enforce Room schema + apply description batches.")
    ap.add_argument("--db", default="app/src/main/assets/prebuilt_tokens.db", help="Path to prebuilt_tokens.db (assets)")
    ap.add_argument("--patch", nargs="+", required=True, help="One or more JSON patch files")
    ap.add_argument("--bump-user-version", action="store_true", help="Increase PRAGMA user_version by 1 after patch")
    ap.add_argument("--set-user-version", type=int, default=None, help="Set PRAGMA user_version explicitly after patch")
    ap.add_argument("--dry-run", action="store_true", help="Validate/Count only, no DB writes")
    args = ap.parse_args()

    db_path = args.db
    if not os.path.exists(db_path):
        raise SystemExit(f"DB not found: {db_path}")

    # Backup
    backup_path = db_path + f".bak_{int(time.time())}"
    shutil.copy2(db_path, backup_path)
    print(f"[backup] created: {backup_path}")

    conn = connect_db(db_path)

    # Ensure schema matches Room
    ensure_schema(conn)

    # Apply patches
    total_updated = total_missing = total_skipped = 0
    for p in args.patch:
        if not os.path.exists(p):
            raise SystemExit(f"Patch file not found: {p}")
        patch = load_patch_file(p)
        u, m, s = apply_patches(conn, patch, dry_run=args.dry_run)
        print(f"[patch] {p}: updated={u}, missing_in_db={m}, skipped={s}")
        total_updated += u
        total_missing += m
        total_skipped += s

    # user_version handling
    if not args.dry_run:
        if args.set_user_version is not None:
            set_user_version(conn, args.set_user_version)
            print(f"[user_version] set to {args.set_user_version}")
        elif args.bump_user_version:
            v = current_user_version(conn)
            set_user_version(conn, v + 1)
            print(f"[user_version] bumped {v} -> {v+1}")

        conn.commit()

    # Final sanity print
    ok, reason = schema_is_room_compatible(conn)
    print(f"[schema] final: {ok} ({reason})")
    print(f"[done] total updated={total_updated}, missing_in_db={total_missing}, skipped={total_skipped}")

    conn.close()

if __name__ == "__main__":
    main()
