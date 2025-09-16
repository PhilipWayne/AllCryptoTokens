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
MAX_REQUESTS_PER_MIN = 4              # CoinGecko is quite strict; keep this conservative
SPACING_SEC = 60.0 / MAX_REQUESTS_PER_MIN

MAX_RETRIES = 6
BASE_BACKOFF_SEC = 2.0
MAX_BACKOFF_SEC = 90.0
TIMEOUT_SEC = 25

# 429 batching (extra pause per item; leave 0 unless you still hit 429 too often)
BATCH_PAUSE_SEC = 1.0

SESSION = requests.Session()
SESSION.headers.update({
    "Accept": "application/json",
    "User-Agent": "AllCryptoTokensPrebuilder/1.0 (+offline preload)"
})

_last_call_ts = 0.0


def _throttle():
    """Simple client-side limiter: spacing between any HTTP requests."""
    global _last_call_ts
    now = time.time()
    delta = now - _last_call_ts
    if delta < SPACING_SEC:
        time.sleep(SPACING_SEC - delta)
    _last_call_ts = time.time()

def chunked(xs, n):
    for i in range(0, len(xs), n):
        yield xs[i:i+n]

def request_json(url: str, params: Optional[Dict[str, Any]] = None) -> Any:
    """HTTP GET with polite spacing + backoff for 429/5xx + transient errors."""
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
                    wait = float(retry_after)
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
    # make write conflicts much less likely
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
# Crypto.com → symbols (base)
# =============================
def fetch_crypto_com_symbols() -> List[str]:
    """
    Returns a **unique, sorted list of base symbols** (uppercase)
    from Crypto.com Exchange instruments.
    Source: Exchange Institutional REST v1 GET
      https://api.crypto.com/exchange/v1/public/get-instruments
    """
    url = "https://api.crypto.com/exchange/v1/public/get-instruments"
    data = request_json(url) or {}
    # v1 REST response: { code, result: { data: [ ... ] } }
    items = ((data.get("result") or {}).get("data")) or []

    bases: set[str] = set()
    for it in items:
        # observed fields in docs: symbol, inst_type, base_ccy, quote_ccy, ...
        inst_type = (it.get("inst_type") or "").upper()
        symbol_name = (it.get("symbol") or "")   # e.g. "BTC_USDT" or "BTCUSD-PERP"
        base = (it.get("base_ccy") or "").upper()

        # Keep SPOT / CCY pairs only. If inst_type missing, heuristically accept pairs with '_' in name.
        if inst_type in ("SPOT", "CCY_PAIR") or "_" in symbol_name:
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
    Heuristic: choose first coin in CoinGecko list whose symbol matches.
    """
    url = "https://api.coingecko.com/api/v3/coins/list"
    coins = request_json(url + "?include_platform=false") or []
    by_sym: Dict[str, List[Dict[str, Any]]] = {}
    for c in coins:
        sym = (c.get("symbol") or "").upper()
        by_sym.setdefault(sym, []).append(c)

    mapped: List[Tuple[str, str, str]] = []
    missing: List[str] = []
    for sym in symbols:
        lst = by_sym.get(sym.upper())
        if not lst:
            missing.append(sym)
            continue
        chosen = lst[0]  # simple heuristic; good enough for prebuild
        mapped.append((chosen["id"], chosen["symbol"], chosen.get("name", chosen["symbol"])))
    print(f"[map] mapped {len(mapped)} / {len(symbols)} symbols to CoinGecko ids; "
          f"missing {len(missing)}", flush=True)
    if missing:
        print(f"[map] e.g. missing: {missing[:20]}…", flush=True)
    return mapped

# =============================
# Seed rows (preserve existing on resume)
# =============================
def seed_rows_from_crypto_com(db: sqlite3.Connection, resume: bool):
    symbols = fetch_crypto_com_symbols()
    rows = map_symbols_to_cg_ids(symbols)  # (cgId, symbol, name)

    cur = db.cursor()
    UPSERT = """
    INSERT INTO tokens (cgId, symbol, name, description, imageUrl, updatedAt)
    VALUES (?, ?, ?, COALESCE((SELECT description FROM tokens t WHERE t.cgId = ?), NULL),
                 COALESCE((SELECT imageUrl    FROM tokens t WHERE t.cgId = ?), NULL),
                 COALESCE((SELECT updatedAt   FROM tokens t WHERE t.cgId = ?), 0))
    ON CONFLICT(cgId) DO UPDATE SET
        symbol = excluded.symbol,
        name   = excluded.name;
    """

    buf = []
    inserted = 0
    for cgId, sym, name in rows:
        buf.append((cgId, sym, name, cgId, cgId, cgId))
        if len(buf) >= 500:
            cur.executemany(UPSERT, buf)
            db.commit()
            inserted += len(buf)
            print(f"[seed] upserted: {inserted}", flush=True)
            buf.clear()
    if buf:
        cur.executemany(UPSERT, buf)
        db.commit()
        inserted += len(buf)
        print(f"[seed] upserted: {inserted}", flush=True)

# =============================
# Enrich details
# =============================
def _pick_image(img_obj: Dict[str, Any]) -> Optional[str]:
    return img_obj.get("large") or img_obj.get("small") or img_obj.get("thumb")

def list_ids_needing_enrich(db: sqlite3.Connection) -> List[str]:
    cur = db.execute("""
        SELECT cgId
        FROM tokens
        WHERE description IS NULL OR description = ''
              OR imageUrl IS NULL OR imageUrl = ''
    """)
    return [row[0] for row in cur.fetchall()]


def enrich_images_via_markets(db: sqlite3.Connection, ids: List[str]):
    """
    Use /coins/markets to fetch images in batches (<=250 ids per call).
    This massively reduces 429s because we don't fetch images per id.
    Only updates rows where imageUrl is NULL/empty.
    """
    to_fill = []
    cur = db.execute("SELECT cgId FROM tokens WHERE (imageUrl IS NULL OR imageUrl = '') AND cgId IN ({})"
                     .format(",".join("?"*len(ids))), ids)
    to_fill = [row[0] for row in cur.fetchall()]
    if not to_fill:
        return

    processed = 0
    now = int(time.time())

    for batch in chunked(to_fill, 200):  # 200–250 is safe
        # markets endpoint accepts CSV of ids
        url = "https://api.coingecko.com/api/v3/coins/markets"
        params = {
            "vs_currency": "usd",
            "order": "market_cap_desc",
            "per_page": 250,
            "page": 1,
            "sparkline": "false",
            "ids": ",".join(batch)
        }
        try:
            arr = request_json(url, params=params) or []
            # arr = [{id, symbol, name, image, ...}, ...]
            with db:
                for it in arr:
                    cid = it.get("id")
                    img = it.get("image")
                    if cid and img:
                        db.execute(
                            "UPDATE tokens SET imageUrl=?, updatedAt=? WHERE cgId=?",
                            (img, now, cid)
                        )
            processed += len(arr)
            print(f"[markets] images updated: {processed}/{len(to_fill)}", flush=True)
            # tiny courtesy pause between batches
            time.sleep(1.0)
        except Exception as e:
            print(f"[warn] markets batch failed ({len(batch)} ids): {e}", flush=True)
            # keep going



def enrich_details(db: sqlite3.Connection, ids: Iterable[str], skip_images: bool):
    ids = list(ids)

    # 1) Fill images in bulk first (unless user asked to skip images)
    if not skip_images and ids:
        enrich_images_via_markets(db, ids)

    # 2) Now fetch descriptions per-id (slow, throttled), skipping rows already filled
    cur = db.cursor()
    cur2 = db.cursor()
    processed = 0
    now = int(time.time())

    # Only rows that still need description
    need_desc = []
    q = db.execute("""
        SELECT cgId FROM tokens
        WHERE (description IS NULL OR description = '')
          AND cgId IN ({})
    """.format(",".join("?"*len(ids))), ids)
    need_desc = [r[0] for r in q.fetchall()]

    for cid in need_desc:
        url = (
            f"https://api.coingecko.com/api/v3/coins/{cid}"
            "?localization=false&tickers=false&market_data=false"
            "&community_data=false&developer_data=false&sparkline=false"
        )
        try:
            data = request_json(url) or {}
            desc_raw = ((data.get("description") or {}).get("en")) or None
            desc = strip_html(desc_raw) if desc_raw else None

            # keep whatever image we may already have from markets
            cur.execute(
                "UPDATE tokens SET description=?, updatedAt=? WHERE cgId=?",
                (desc, now, cid)
            )
            processed += 1
            if processed % 25 == 0:
                db.commit()
                print(f"[enrich] descriptions updated: {processed}/{len(need_desc)}", flush=True)
        except Exception as e:
            print(f"[warn] detail error for {cid}: {e}", flush=True)

    db.commit()
    print(f"[enrich] done. descriptions updated={processed}, images were filled via markets.", flush=True)


# =============================
# Main
# =============================
def main():
    args = parse_args()
    print(f"[boot] out={args.out} skip_images={args.skip_images} "
          f"resume={args.resume} force={args.force}", flush=True)

    db = open_db(args.out, resume=args.resume, force=args.force)

    if args.resume and os.path.exists(args.out):
        # Resume: just enrich what’s missing
        ids = list_ids_needing_enrich(db)
        print(f"[resume] ids needing enrich = {len(ids)}", flush=True)
        if ids:
            enrich_details(db, ids, skip_images=args.skip_images)
        else:
            print("[resume] nothing to do.", flush=True)
        db.close()
        return

    # Fresh (or forced) build: seed from Crypto.com → map to CG → upsert → enrich
    print("[step] seeding rows from Crypto.com universe…", flush=True)
    seed_rows_from_crypto_com(db, resume=False)

    ids = list_ids_needing_enrich(db)
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
