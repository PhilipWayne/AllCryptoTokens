import json
import os
import re
import time
import html
import sqlite3
from typing import Any, Dict, List, Optional, Set

import requests

# ================= CONFIG =================
CRYPTOCOM_TICKER_URL = "https://api.crypto.com/v2/public/get-ticker"
COINGECKO_BASE = "https://api.coingecko.com/api/v3"

DB_PATH = "allcryptotokens.db"
STATE_PATH = "build_state.json"
OVERRIDES_PATH = "overrides.json"
REPORT_PATH = "mapping_report.json"

SLEEP_SEC = 12.0  # HARD REQUIREMENT
COMMIT_EVERY = 5

HEADERS = {"accept": "application/json"}


# ================= HELPERS =================
def clean_text(s: str, limit: int = 2000) -> str:
    s = html.unescape(s or "")
    s = re.sub(r"<[^>]+>", " ", s)
    s = re.sub(r"\s+", " ", s).strip()
    return s[:limit]


def get_json(url: str) -> Dict[str, Any]:
    r = requests.get(url, headers=HEADERS, timeout=60)
    if r.status_code != 200:
        raise RuntimeError(f"HTTP {r.status_code} for {url}: {r.text[:200]}")
    return r.json()


def ensure_schema(conn: sqlite3.Connection) -> None:
    conn.execute("""
    CREATE TABLE IF NOT EXISTS tokens (
        cgId TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        symbol TEXT NOT NULL,
        description TEXT,
        imageUrl TEXT,
        updatedAt INTEGER NOT NULL,
        sourceVersion INTEGER NOT NULL
    )
    """)
    conn.commit()


def upsert(conn: sqlite3.Connection, row: Dict[str, Any]) -> None:
    conn.execute("""
    INSERT INTO tokens (cgId, name, symbol, description, imageUrl, updatedAt, sourceVersion)
    VALUES (?, ?, ?, ?, ?, ?, ?)
    ON CONFLICT(cgId) DO UPDATE SET
        name=excluded.name,
        symbol=excluded.symbol,
        imageUrl=excluded.imageUrl,
        updatedAt=excluded.updatedAt,
        sourceVersion=excluded.sourceVersion,
        description=CASE
            WHEN excluded.description IS NULL OR TRIM(excluded.description) = ''
            THEN tokens.description
            ELSE excluded.description
        END
    """, (
        row["cgId"],
        row["name"],
        row["symbol"],
        row["description"],
        row["imageUrl"],
        row["updatedAt"],
        row["sourceVersion"],
    ))


# ================= CRYPTO.COM =================
def fetch_cryptocom_symbols() -> Set[str]:
    print("1) Fetching Crypto.com tickers...", flush=True)
    data = get_json(CRYPTOCOM_TICKER_URL)

    tickers = data.get("result", {}).get("data", [])
    if not isinstance(tickers, list):
        raise RuntimeError("Unexpected Crypto.com ticker format")

    symbols: Set[str] = set()
    for t in tickers:
        name = t.get("i")  # instrument_name, e.g. BTC_USDT
        if not name or "_" not in name:
            continue
        base = name.split("_")[0].upper()
        symbols.add(base)

    print(f"Crypto.com base symbols: {len(symbols)}", flush=True)
    return symbols


# ================= COINGECKO =================
def build_cg_index() -> Dict[str, List[Dict[str, str]]]:
    print("2) Building CoinGecko index...", flush=True)
    coins = get_json(f"{COINGECKO_BASE}/coins/list")
    idx: Dict[str, List[Dict[str, str]]] = {}
    for c in coins:
        sym = (c.get("symbol") or "").upper()
        if sym:
            idx.setdefault(sym, []).append(c)
    return idx


def main():
    overrides = {}
    if os.path.exists(OVERRIDES_PATH):
        overrides = json.load(open(OVERRIDES_PATH))

    crypto_syms = fetch_cryptocom_symbols()
    cg_index = build_cg_index()

    resolved_ids: List[str] = []
    for sym in sorted(crypto_syms):
        if sym in overrides:
            resolved_ids.append(overrides[sym])
            continue
        cands = cg_index.get(sym, [])
        if len(cands) == 1:
            resolved_ids.append(cands[0]["id"])

    print(f"3) Will fetch CoinGecko tokens: {len(resolved_ids)}", flush=True)

    conn = sqlite3.connect(DB_PATH)
    ensure_schema(conn)

    now = int(time.time() * 1000)
    done = 0

    for cid in resolved_ids:
        print(f"Fetching CoinGecko: {cid}", flush=True)
        detail = get_json(f"{COINGECKO_BASE}/coins/{cid}?localization=false&tickers=false&market_data=false&community_data=false&developer_data=false")

        desc = clean_text(detail.get("description", {}).get("en", ""))
        if not desc:
            cats = detail.get("categories", [])
            desc = f"{detail.get('name')} is a cryptocurrency used in {', '.join(cats[:4])}."

        upsert(conn, {
            "cgId": cid,
            "name": detail.get("name"),
            "symbol": detail.get("symbol", "").upper(),
            "description": desc,
            "imageUrl": detail.get("image", {}).get("large"),
            "updatedAt": now,
            "sourceVersion": 1,
        })

        done += 1
        if done % COMMIT_EVERY == 0:
            conn.commit()

        time.sleep(SLEEP_SEC)

    conn.commit()
    conn.execute("PRAGMA user_version = 3;")
    conn.commit()
    conn.close()

    print("DONE. DB saved.", flush=True)


if __name__ == "__main__":
    main()
