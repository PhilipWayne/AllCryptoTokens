#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import json
import sqlite3
from pathlib import Path

ROOT = Path(__file__).resolve().parent

TOKENS_JSON = ROOT / "offline_export" / "tokens_enriched.json"
OUT_DB = ROOT / "offline_export" / "allcryptotokens.db"

def main():
    if not TOKENS_JSON.exists():
        raise SystemExit(f"Missing: {TOKENS_JSON}")

    data = json.loads(TOKENS_JSON.read_text(encoding="utf-8"))
    if not isinstance(data, list):
        raise SystemExit("tokens_enriched.json is not a list")

    # Recreate DB from scratch
    if OUT_DB.exists():
        OUT_DB.unlink()

    conn = sqlite3.connect(str(OUT_DB))
    cur = conn.cursor()

    # IMPORTANT:
    # Table name/columns must match your Room Entity.
    # This schema assumes:
    #   table: tokens
    #   columns: ticker (PK), name, description, icon_file
    cur.execute("""
        CREATE TABLE tokens (
            ticker TEXT NOT NULL PRIMARY KEY,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            icon_file TEXT
        )
    """)

    cur.execute("CREATE INDEX idx_tokens_name ON tokens(name)")
    cur.execute("CREATE INDEX idx_tokens_ticker ON tokens(ticker)")

    rows = []
    for t in data:
        ticker = (t.get("ticker") or "").upper().strip()
        name = (t.get("name") or "").strip()
        desc = (t.get("description") or "").strip()
        icon = (t.get("icon_file") or "").strip()

        if not ticker or not name:
            continue

        rows.append((ticker, name, desc, icon))

    cur.executemany(
        "INSERT OR REPLACE INTO tokens (ticker, name, description, icon_file) VALUES (?, ?, ?, ?)",
        rows
    )

    conn.commit()
    conn.close()

    print(f"[DONE] DB created: {OUT_DB}")
    print(f"[DONE] Rows inserted: {len(rows)}")

if __name__ == "__main__":
    main()
