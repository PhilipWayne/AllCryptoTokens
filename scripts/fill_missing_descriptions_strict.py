#!/usr/bin/env python3
import argparse
import html
import json
import re
import sqlite3
import time
from typing import Dict, List, Optional, Tuple
from urllib.parse import urljoin, urlparse

import requests


COINGECKO = "https://api.coingecko.com/api/v3"
TIMEOUT = 25


def strip_html(s: str) -> str:
    s = re.sub(r"<[^>]*>", "", s or "")
    return html.unescape(s).strip()


def norm_space(s: str) -> str:
    return re.sub(r"\s+", " ", (s or "")).strip()


def get_missing_rows(db_path: str) -> List[Tuple[str, str, str]]:
    conn = sqlite3.connect(db_path)
    rows = conn.execute("""
        SELECT cgId, symbol, name
        FROM tokens
        WHERE description IS NULL OR description=''
        ORDER BY name COLLATE NOCASE
    """).fetchall()
    conn.close()
    return [(r[0], r[1], r[2]) for r in rows if r and r[0]]


def fetch_coingecko_coin(session: requests.Session, cg_id: str) -> dict:
    url = f"{COINGECKO}/coins/{cg_id}"
    params = {
        "localization": "false",
        "tickers": "false",
        "market_data": "false",
        "community_data": "false",
        "developer_data": "false",
        "sparkline": "false",
    }
    r = session.get(url, params=params, timeout=TIMEOUT)
    r.raise_for_status()
    return r.json()


def extract_meta_description(html_text: str) -> str:
    # <meta name="description" content="...">
    m = re.search(
        r'<meta[^>]+name=["\']description["\'][^>]+content=["\']([^"\']+)["\']',
        html_text, flags=re.IGNORECASE
    )
    if m:
        return norm_space(strip_html(m.group(1)))
    # og:description
    m2 = re.search(
        r'<meta[^>]+property=["\']og:description["\'][^>]+content=["\']([^"\']+)["\']',
        html_text, flags=re.IGNORECASE
    )
    if m2:
        return norm_space(strip_html(m2.group(1)))
    return ""


def extract_visible_text(html_text: str, max_chars: int = 2500) -> str:
    """
    Very lightweight extraction: take first meaningful <p> blocks.
    Not perfect, but works well enough for most "About" pages.
    """
    # Grab paragraph-ish content
    ps = re.findall(r"<p[^>]*>(.*?)</p>", html_text, flags=re.IGNORECASE | re.DOTALL)
    chunks: List[str] = []
    for p in ps[:25]:
        t = norm_space(strip_html(p))
        # skip tiny junk
        if len(t) < 40:
            continue
        chunks.append(t)
        if sum(len(x) for x in chunks) >= max_chars:
            break
    return norm_space(" ".join(chunks))


def safe_get(session: requests.Session, url: str) -> str:
    r = session.get(url, timeout=TIMEOUT, headers={"User-Agent": "AllCryptoTokens/1.0"})
    r.raise_for_status()
    return r.text


def same_domain(base: str, candidate: str) -> bool:
    try:
        b = urlparse(base)
        c = urlparse(candidate)
        return b.netloc and (b.netloc == c.netloc)
    except Exception:
        return False


def candidate_pages(homepage: str) -> List[str]:
    """
    Try likely 'about' pages without going crazy.
    """
    if not homepage:
        return []
    homepage = homepage.strip()
    if not homepage.startswith("http"):
        homepage = "https://" + homepage

    base = homepage.rstrip("/") + "/"
    candidates = [homepage]
    # common paths
    for path in ["about", "about-us", "docs", "documentation", "whitepaper", "learn", "what-is"]:
        candidates.append(urljoin(base, path))
    # avoid duplicates
    out = []
    seen = set()
    for u in candidates:
        if u not in seen:
            out.append(u)
            seen.add(u)
    return out


def github_readme_text(session: requests.Session, repo_url: str) -> str:
    """
    Attempt to fetch README via GitHub web URL (no token).
    Not guaranteed, but often works.
    """
    # examples:
    # https://github.com/org/repo
    # build raw README candidates
    try:
        p = urlparse(repo_url)
        parts = [x for x in p.path.split("/") if x]
        if len(parts) < 2:
            return ""
        org, repo = parts[0], parts[1]
    except Exception:
        return ""

    # Try raw README locations (main/master)
    raw_candidates = [
        f"https://raw.githubusercontent.com/{org}/{repo}/main/README.md",
        f"https://raw.githubusercontent.com/{org}/{repo}/master/README.md",
        f"https://raw.githubusercontent.com/{org}/{repo}/main/readme.md",
        f"https://raw.githubusercontent.com/{org}/{repo}/master/readme.md",
    ]
    for u in raw_candidates:
        try:
            txt = safe_get(session, u)
            # strip markdown crud a bit
            txt = re.sub(r"!\[.*?\]\(.*?\)", "", txt)  # images
            txt = re.sub(r"\[([^\]]+)\]\(([^)]+)\)", r"\1", txt)  # links
            txt = norm_space(txt)
            # take first 2000 chars
            return txt[:2000]
        except Exception:
            continue
    return ""


def resolve_description_for_token(
        session: requests.Session,
        cg_id: str,
        symbol: str,
        name: str,
        min_len: int,
        delay: float
) -> Tuple[str, List[str]]:
    """
    Returns (description, sources_used).
    Raises if cannot resolve.
    """
    sources: List[str] = []

    coin = fetch_coingecko_coin(session, cg_id)
    cg_desc = norm_space(strip_html(((coin.get("description") or {}).get("en")) or ""))
    if cg_desc and len(cg_desc) >= min_len:
        sources.append(f"coingecko:/coins/{cg_id}")
        return cg_desc, sources

    # pick official homepage from CoinGecko links
    links = coin.get("links") or {}
    homepages = [x for x in (links.get("homepage") or []) if x and isinstance(x, str)]
    homepage = homepages[0] if homepages else ""
    repos = links.get("repos_url") or {}
    gh_repos = repos.get("github") or []
    github = gh_repos[0] if gh_repos else ""

    # Try homepage pages (meta description -> visible text)
    if homepage:
        for page in candidate_pages(homepage):
            try:
                html_text = safe_get(session, page)
                sources.append(page)
                meta = extract_meta_description(html_text)
                if meta and len(meta) >= min_len:
                    return meta, sources

                body = extract_visible_text(html_text)
                if body and len(body) >= min_len:
                    return body, sources
            except Exception:
                pass
            time.sleep(delay)

    # Try GitHub README if available
    if github:
        try:
            txt = github_readme_text(session, github)
            if txt and len(txt) >= min_len:
                sources.append(github)
                return txt, sources
        except Exception:
            pass

    # If still not enough, fail hard (no placeholders)
    raise RuntimeError(f"No description found meeting min_len={min_len} for {name} ({symbol}) [{cg_id}]")


def main():
    ap = argparse.ArgumentParser(
        description="Strictly fill missing token descriptions. No placeholders. "
                    "Will FAIL if any token cannot be resolved."
    )
    ap.add_argument("--db", required=True, help="Path to prebuilt_tokens.db")
    ap.add_argument("--out", required=True, help="Output JSON {cgId: description}")
    ap.add_argument("--sources-out", default="scripts/sources_map.json",
                    help="Output JSON {cgId: [sources...]}")
    ap.add_argument("--delay", type=float, default=12.0, help="Seconds between requests to avoid 429")
    ap.add_argument("--min-len", type=int, default=250, help="Minimum acceptable description length")
    ap.add_argument("--max", type=int, default=0, help="Limit count for testing (0 = all)")
    args = ap.parse_args()

    missing = get_missing_rows(args.db)
    if args.max and args.max > 0:
        missing = missing[:args.max]

    print(f"[info] tokens missing description: {len(missing)}")

    session = requests.Session()
    session.headers.update({
        "Accept": "application/json",
        "User-Agent": "AllCryptoTokens-StrictDescriptionFiller/1.0"
    })

    out: Dict[str, str] = {}
    sources_map: Dict[str, List[str]] = {}
    unresolved: List[Dict[str, str]] = []

    total = len(missing)
    for idx, (cg_id, symbol, name) in enumerate(missing, 1):
        try:
            desc, sources = resolve_description_for_token(
                session=session,
                cg_id=cg_id,
                symbol=symbol,
                name=name,
                min_len=args.min_len,
                delay=args.delay
            )
            out[cg_id] = desc
            sources_map[cg_id] = sources
            print(f"[ok] {idx}/{total} {name} ({symbol}) len={len(desc)}")
        except Exception as e:
            unresolved.append({"cgId": cg_id, "symbol": symbol, "name": name, "error": str(e)})
            print(f"[FAIL] {idx}/{total} {name} ({symbol}) -> {e}")
        time.sleep(args.delay)

    with open(args.out, "w", encoding="utf-8") as f:
        json.dump(out, f, ensure_ascii=False, indent=2)

    with open(args.sources_out, "w", encoding="utf-8") as f:
        json.dump(sources_map, f, ensure_ascii=False, indent=2)

    if unresolved:
        unresolved_path = "scripts/unresolved.json"
        with open(unresolved_path, "w", encoding="utf-8") as f:
            json.dump(unresolved, f, ensure_ascii=False, indent=2)
        raise SystemExit(f"[STOP] Unresolved tokens: {len(unresolved)}. See {unresolved_path}")

    print(f"[done] wrote descriptions: {args.out}")
    print(f"[done] wrote sources: {args.sources_out}")


if __name__ == "__main__":
    main()
