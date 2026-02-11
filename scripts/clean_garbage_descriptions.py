#!/usr/bin/env python3
import argparse
import re
import sqlite3
import time
import shutil

BAD_SUBSTRINGS = [
    "@property",
    "--tw-",
    "Exchange Plus",
    "CEX.IO",
    "airdrop",
    "Margin Trading",
]

# склейки/мусорные паттерны
BAD_REGEX = [
    r"DocsLearn",
    r"GithubImplement",
    r"TelegramChat",
    r"DiscordHelp",
    r"\+\d{2,}",                  # +13K / +312K etc
    r"[a-z][A-Z][a-z]",            # признак CamelCase внутри текста (склейка)
    r"(?:\w{3,})(?:\w{3,}){6,}",   # очень длинные "слепленные" слова
]

def is_garbage(text: str) -> bool:
    if not text:
        return False
    t = text.strip()

    # быстрые стоп-слова
    for s in BAD_SUBSTRINGS:
        if s.lower() in t.lower():
            return True

    # regex признаки мусора
    hits = 0
    for pat in BAD_REGEX:
        if re.search(pat, t):
            hits += 1
    # если несколько признаков - мусор
    return hits >= 2

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--db", required=True)
    ap.add_argument("--backup", action="store_true")
    ap.add_argument("--bump-user-version", action="store_true")
    args = ap.parse_args()

    db = args.db

    if args.backup:
        ts = time.strftime("%Y%m%d-%H%M%S")
        bak = f"{db}.bak-garbage-{ts}"
        shutil.copy2(db, bak)
        print(f"[ok] backup: {bak}")

    conn = sqlite3.connect(db)
    rows = conn.execute("""
        SELECT cgId, description
        FROM tokens
        WHERE description IS NOT NULL AND description != ''
    """).fetchall()

    bad_ids = []
    for cgId, desc in rows:
        if is_garbage(desc or ""):
            bad_ids.append(cgId)

    bad_ids = sorted(set(bad_ids))
    print(f"[info] garbage-like descriptions found: {len(bad_ids)}")

    if bad_ids:
        conn.execute("BEGIN;")
        for cid in bad_ids:
            conn.execute("UPDATE tokens SET description='' WHERE cgId=?", (cid,))
        if args.bump_user_version:
            uv = conn.execute("PRAGMA user_version;").fetchone()[0] or 0
            conn.execute(f"PRAGMA user_version={int(uv)+1};")
            print(f"[ok] user_version: {uv} -> {uv+1}")
        conn.execute("COMMIT;")

    remaining = conn.execute(
        "SELECT COUNT(*) FROM tokens WHERE description IS NULL OR description='';"
    ).fetchone()[0]
    conn.close()

    print(f"[ok] cleared: {len(bad_ids)}")
    print(f"[ok] remaining missing now: {remaining}")
    if bad_ids:
        print("[list] cgIds cleared (first 80):")
        for x in bad_ids[:80]:
            print(" -", x)

if __name__ == "__main__":
    main()
