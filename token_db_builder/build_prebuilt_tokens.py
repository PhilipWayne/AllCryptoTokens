#!/usr/bin/env python3
import argparse
import html
import os
import random
import re
import sqlite3
import sys
import time
from typing import Any, Dict, Iterable, List, Optional, Tuple

import requests

# =============================
# Throttle / Retry configuration
# =============================
# Hard spacing between ANY HTTP requests (CoinGecko rate limits are strict).
SPACING_SEC = 12.0

MAX_RETRIES = 6
BASE_BACKOFF_SEC = 2.0
MAX_BACKOFF_SEC = 60.0
TIMEOUT_SEC = 20

# Optional extra pause per item (usually keep 0 when SPACING_SEC is already large)
BATCH_PAUSE_SEC = 0.0

SESSION = requests.Session()
SESSION.headers.update({
    "Accept": "application/json",
    "User-Agent": "AllCryptoTokensPrebuilder/1.0 (+offline preload)"
})

_last_call_ts = 0.0


def _throttle():
    """Client-side limiter: fixed spacing between HTTP requests."""
    global _last_call_ts
    now = time.time()
    delta = now - _last_call_ts
    if delta < SPACING_SEC:
        time.sleep(SPACING_SEC - delta)
    _last_call_ts = time.time()


def request_json(url: str, params: Optional[Dict[str, Any]] = None) -> Any:
    """HTTP GET with spacing + retry/backoff for 429/5xx + transient errors."""
    attempt = 0
    while True:
        attempt += 1
        _throttle()
        try:
            resp = SESSION.get(url, params=params, timeout=TIMEOUT_SEC)

            # Handle 429 / server hiccups
            if resp.status_code == 429 or 500 <= resp.status_code < 600:
                retry_after = resp.headers.get("Retry-After")
                if retry_after:
                    try:
                        wait = float(retry_after)
                    except ValueError:
                        wait = 10.0
                else:
                    backoff = min(MAX_BACKOFF_SEC, BASE_BACKOFF_SEC * (2 ** (attempt - 1)))
                    wait = backoff + random.random() * 0.4

                if attempt <= MAX_RETRIES:
                    print(f"[warn] {resp.status_code} on {url} — retrying in {wait:.1f}s "
                          f"(attempt {attempt}/{MAX_RETRIES})", flush=True)
                    time.sleep(wait)
                    continue

                resp.raise_for_status()

            resp.raise_for_status()
            return resp.json()

        except (requests.Timeout, requests.ConnectionError) as e:
            if attempt <= MAX_RETRIES:
                backoff = min(MAX_BACKOFF_SEC, BASE_BACKOFF_SEC * (2 ** (attempt - 1)))
                wait = backoff + random.random() * 0.4
                print(f"[warn] network error: {e} — retrying in {wait:.1f}s "
                      f"(attempt {attempt}/{MAX_RETRIES})", flush=True)
                time.sleep(wait)
                continue
            raise


# =============================
# CLI
# =============================
def parse_args():
    p = argparse.ArgumentParser(
        description="Build prebuilt_tokens.db with ONLY Crypto.com tokens (name, symbol, description, image)."
    )
    p.add_argument("--out", default="app/src/main/assets/prebuilt_tokens.db",
                   help="Output sqlite path")
    p.add_argument("--skip-images", action="store_true",
                   help="Skip image lookups (descriptions only)")
    p.add_argument("--resume", action="store_true",
                   help="Resume: keep existing rows and only fill missing description/image")
    p.add_argument("--force", action="store_true",
                   help="Overwrite output file (ignored if --resume)")
    return p.parse_args()


# =============================
# SQLite helpers / schema
# =============================
TOKENS_SQL = """
CREATE TABLE IF NOT EXISTS tokens(
  cgId        TEXT PRIMARY KEY,
  symbol      TEXT,
  name        TEXT,
  description TEXT,
  imageUrl    TEXT,
  updatedAt   INTEGER
);
"""
IDX1 = "CREATE INDEX IF NOT EXISTS idx_tokens_symbol ON tokens(symbol);"
IDX2 = "CREATE INDEX IF NOT EXISTS idx_tokens_name   ON tokens(name);"


def _ensure_dir(path: str):
    d = os.path.dirname(os.path.abspath(path))
    if d and not os.path.exists(d):
        os.makedirs(d, exist_ok=True)


def open_db(path: str, resume: bool, force: bool) -> sqlite3.Connection:
    _ensure_dir(path)
    if not resume and force and os.path.exists(path):
        os.remove(path)

    db = sqlite3.connect(path)
    db.execute("PRAGMA journal_mode=WAL;")
    db.execute("PRAGMA synchronous=NORMAL;")
    db.execute("PRAGMA busy_timeout=30000;")

    db.execute(TOKENS_SQL)
    db.execute(IDX1)
    db.execute(IDX2)
    return db


def strip_html(s: Optional[str]) -> Optional[str]:
    if not s:
        return s
    s = re.sub(r"<[^>]*>", "", s)
    return html.unescape(s).strip()


# =============================
# Crypto.com → base symbols
# =============================
def fetch_crypto_com_symbols() -> List[str]:
    """
    Returns a unique, sorted list of base symbols (uppercase) from Crypto.com Exchange instruments.

    Endpoint:
      https://api.crypto.com/exchange/v1/public/get-instruments

    Observed instrument fields can include:
      inst_type, symbol, base_ccy, quote_ccy, ...
    """
    url = "https://api.crypto.com/exchange/v1/public/get-instruments"
    data = request_json(url) or {}
    items = ((data.get("result") or {}).get("data")) or []

    bases: set[str] = set()

    for it in items:
        inst_type = (it.get("inst_type") or it.get("instrument_type") or it.get("type") or "").upper()
        symbol_name = (it.get("symbol") or it.get("instrument_name") or it.get("instrument") or "")
        base = (it.get("base_ccy") or it.get("base_currency") or it.get("base") or "")

        base = base.upper().strip()
        symbol_name = symbol_name.strip()

        # Keep spot/ccy pairs only, exclude derivatives
        # Some responses label spot as "CCY_PAIR" rather than "SPOT"
        is_pair_like = ("_" in symbol_name) and ("-PERP" not in symbol_name.upper())
        if inst_type in ("SPOT", "CCY_PAIR") or is_pair_like:
            if base:
                bases.add(base)

    out = sorted(bases)
    print(f"[crypto.com] base symbols collected: {len(out)}", flush=True)
    return out


# =============================
# Map symbols → CoinGecko ids
# =============================
def map_symbols_to_cg_ids(symbols: List[str]) -> List[Tuple[str, str, str]]:
    """
    Returns list of (cgId, symbol, name) for the given symbols.
    Heuristic: choose the first CoinGecko coin whose symbol matches.
    """
    url = "https://api.coingecko.com/api/v3/coins/list"
    coins = request_json(url, params={"include_platform": "false"}) or []

    by_sym: Dict[str, List[Dict[str, Any]]] = {}
    for c in coins:
        sym = (c.get("symbol") or "").upper()
        if not sym:
            continue
        by_sym.setdefault(sym, []).append(c)

    mapped: List[Tuple[str, str, str]] = []
    missing: List[str] = []

    for sym in symbols:
        lst = by_sym.get(sym.upper())
        if not lst:
            missing.append(sym)
            continue
        chosen = lst[0]  # simple heuristic
        mapped.append((chosen["id"], chosen["symbol"], chosen.get("name", chosen["symbol"])))

    print(f"[map] mapped {len(mapped)} / {len(symbols)} symbols to CoinGecko ids; missing {len(missing)}", flush=True)
    if missing:
        print(f"[map] missing examples: {missing[:20]}{'…' if len(missing) > 20 else ''}", flush=True)

    return mapped


# =============================
# Seed rows (insert/update)
# =============================
def seed_rows_from_crypto_com(db: sqlite3.Connection):
    symbols = fetch_crypto_com_symbols()
    rows = map_symbols_to_cg_ids(symbols)  # (cgId, symbol, name)

    cur = db.cursor()
    upsert = """
    INSERT INTO tokens (cgId, symbol, name, description, imageUrl, updatedAt)
    VALUES (?, ?, ?,
            COALESCE((SELECT description FROM tokens t WHERE t.cgId = ?), NULL),
            COALESCE((SELECT imageUrl    FROM tokens t WHERE t.cgId = ?), NULL),
            COALESCE((SELECT updatedAt   FROM tokens t WHERE t.cgId = ?), 0))
    ON CONFLICT(cgId) DO UPDATE SET
        symbol = excluded.symbol,
        name   = excluded.name;
    """

    buf = []
    upserted = 0
    for cgId, sym, name in rows:
        buf.append((cgId, sym, name, cgId, cgId, cgId))
        if len(buf) >= 500:
            cur.executemany(upsert, buf)
            db.commit()
            upserted += len(buf)
            print(f"[seed] upserted: {upserted}", flush=True)
            buf.clear()

    if buf:
        cur.executemany(upsert, buf)
        db.commit()
        upserted += len(buf)
        print(f"[seed] upserted: {upserted}", flush=True)


# =============================
# Enrich details
# =============================
def _pick_image(img_obj: Dict[str, Any]) -> Optional[str]:
    if not img_obj:
        return None
    return img_obj.get("large") or img_obj.get("small") or img_obj.get("thumb")


def list_ids_needing_enrich(db: sqlite3.Connection, skip_images: bool) -> List[str]:
    if skip_images:
        cur = db.execute("""
            SELECT cgId
            FROM tokens
            WHERE description IS NULL OR description = ''
        """)
    else:
        cur = db.execute("""
            SELECT cgId
            FROM tokens
            WHERE (description IS NULL OR description = '')
               OR (imageUrl IS NULL OR imageUrl = '')
        """)
    return [row[0] for row in cur.fetchall()]


def enrich_details(db: sqlite3.Connection, ids: Iterable[str], skip_images: bool):
    cur = db.cursor()
    processed = 0
    now = int(time.time())

    for cid in ids:
        url = f"https://api.coingecko.com/api/v3/coins/{cid}"
        params = {
            "localization": "false",
            "tickers": "false",
            "market_data": "false",
            "community_data": "false",
            "developer_data": "false",
            "sparkline": "false",
        }

        try:
            data = request_json(url, params=params) or {}

            desc_raw = ((data.get("description") or {}).get("en")) or None
            desc = strip_html(desc_raw) if desc_raw else None

            img = None
            if not skip_images:
                img = _pick_image((data.get("image") or {}))

            cur.execute(
                "UPDATE tokens SET description=?, imageUrl=?, updatedAt=? WHERE cgId=?",
                (desc, img, now, cid)
            )

            processed += 1
            if processed % 25 == 0:
                db.commit()
                print(f"[enrich] processed={processed}", flush=True)

            if BATCH_PAUSE_SEC:
                time.sleep(BATCH_PAUSE_SEC)

        except Exception as e:
            print(f"[warn] detail error for {cid}: {e}", flush=True)

    db.commit()
    print(f"[enrich] done. total processed={processed}", flush=True)


# =============================
# Main
# =============================
def main():
    args = parse_args()
    print(f"[boot] out={args.out} skip_images={args.skip_images} resume={args.resume} force={args.force}", flush=True)
    print(f"[boot] request spacing = {SPACING_SEC:.1f}s", flush=True)

    db = open_db(args.out, resume=args.resume, force=args.force)

    if args.resume:
        ids = list_ids_needing_enrich(db, skip_images=args.skip_images)
        print(f"[resume] ids needing enrich = {len(ids)}", flush=True)
        if ids:
            enrich_details(db, ids, skip_images=args.skip_images)
        else:
            print("[resume] nothing to do.", flush=True)
        db.close()
        print(f"[done] wrote {args.out}", flush=True)
        return

    print("[step] seeding rows from Crypto.com universe…", flush=True)
    seed_rows_from_crypto_com(db)

    ids = list_ids_needing_enrich(db, skip_images=args.skip_images)
    print(f"[step] enriching details for {len(ids)} ids", flush=True)
    enrich_details(db, ids, skip_images=args.skip_images)

    db.close()
    print(f"[done] wrote {args.out}", flush=True)


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n[cancelled] KeyboardInterrupt", file=sys.stderr)
        sys.exit(130)
