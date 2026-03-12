import os
import shutil
import sqlite3
import time

DB_PATH = os.path.join("app", "src", "main", "assets", "prebuilt_tokens.db")

REPLACEMENTS = [
    {
        "old_cgid": "batcat",
        "new_cgid": "bitcoin",
        "symbol": "btc",
        "name": "Bitcoin",
        "description": "Bitcoin is the original decentralized cryptocurrency designed for peer-to-peer digital payments without a central authority."
    },
    {
        "old_cgid": "bifrost-bridged-eth-bifrost",
        "new_cgid": "ethereum",
        "symbol": "eth",
        "name": "Ethereum",
        "description": "Ethereum is a smart contract blockchain used for decentralized apps, tokens, and on-chain financial activity."
    },
    {
        "old_cgid": "arbitrage-loop",
        "new_cgid": "arbitrum",
        "symbol": "arb",
        "name": "Arbitrum",
        "description": "Arbitrum is an Ethereum layer-2 network designed to make transactions faster and cheaper while staying connected to Ethereum."
    },
    {
        "old_cgid": "binance-peg-cardano",
        "new_cgid": "cardano",
        "symbol": "ada",
        "name": "Cardano",
        "description": "Cardano is a proof-of-stake blockchain focused on scalability, formal methods, and smart contract functionality."
    },
    {
        "old_cgid": "binance-peg-dogecoin",
        "new_cgid": "dogecoin",
        "symbol": "doge",
        "name": "Dogecoin",
        "description": "Dogecoin is a cryptocurrency that started as a meme coin and became widely used for online tipping, trading, and community-driven payments."
    },
    {
        "old_cgid": "binance-peg-polkadot",
        "new_cgid": "polkadot",
        "symbol": "dot",
        "name": "Polkadot",
        "description": "Polkadot is a blockchain network designed to connect multiple chains and let them share security and data."
    },
    {
        "old_cgid": "binance-peg-litecoin",
        "new_cgid": "litecoin",
        "symbol": "ltc",
        "name": "Litecoin",
        "description": "Litecoin is a long-running cryptocurrency created for fast and low-cost digital payments."
    },
    {
        "old_cgid": "binance-peg-xrp",
        "new_cgid": "ripple",
        "symbol": "xrp",
        "name": "XRP",
        "description": "XRP is a digital asset used in the XRP Ledger for transfers, payments, and other blockchain-based financial use cases."
    },
    {
        "old_cgid": "binance-peg-bitcoin-cash",
        "new_cgid": "bitcoin-cash",
        "symbol": "bch",
        "name": "Bitcoin Cash",
        "description": "Bitcoin Cash is a cryptocurrency focused on peer-to-peer electronic payments with larger block capacity than Bitcoin."
    },
    {
        "old_cgid": "binance-peg-filecoin",
        "new_cgid": "filecoin",
        "symbol": "fil",
        "name": "Filecoin",
        "description": "Filecoin is a decentralized storage network where users can pay to store data and providers can earn by supplying storage capacity."
    },
    {
        "old_cgid": "binance-peg-near-protocol",
        "new_cgid": "near",
        "symbol": "near",
        "name": "NEAR Protocol",
        "description": "NEAR Protocol is a smart contract blockchain built for scalable decentralized applications and user-friendly onboarding."
    },
    {
        "old_cgid": "binance-peg-ontology",
        "new_cgid": "ontology",
        "symbol": "ont",
        "name": "Ontology",
        "description": "Ontology is a blockchain platform focused on digital identity, data solutions, and enterprise-oriented infrastructure."
    },
    {
        "old_cgid": "binance-peg-iotex",
        "new_cgid": "iotex",
        "symbol": "iotx",
        "name": "IoTeX",
        "description": "IoTeX is a blockchain project focused on connecting devices, machine data, and decentralized applications."
    },
    {
        "old_cgid": "binance-peg-tezos-token",
        "new_cgid": "tezos",
        "symbol": "xtz",
        "name": "Tezos",
        "description": "Tezos is a proof-of-stake blockchain for smart contracts and digital assets with on-chain governance."
    },
    {
        "old_cgid": "binance-peg-shib",
        "new_cgid": "shiba-inu",
        "symbol": "shib",
        "name": "Shiba Inu",
        "description": "Shiba Inu is a meme cryptocurrency that expanded into a broader ecosystem of tokens, community products, and DeFi features."
    },
    {
        "old_cgid": "allbridge-bridged-sol-near-protocol",
        "new_cgid": "solana",
        "symbol": "sol",
        "name": "Solana",
        "description": "Solana is a high-throughput smart contract blockchain designed for fast transactions and low fees."
    },
    {
        "old_cgid": "tac-bridged-ton-tac",
        "new_cgid": "the-open-network",
        "symbol": "ton",
        "name": "Toncoin",
        "description": "Toncoin is the native coin of The Open Network, a layer-1 blockchain used for payments, apps, and ecosystem activity."
    },
    {
        "old_cgid": "abstract-bridged-usdt-abstract",
        "new_cgid": "tether",
        "symbol": "usdt",
        "name": "Tether",
        "description": "Tether is a stablecoin designed to track the value of the US dollar and is widely used across crypto trading and payments."
    },
    {
        "old_cgid": "arbitrum-bridged-wbtc-arbitrum-one",
        "new_cgid": "wrapped-bitcoin",
        "symbol": "wbtc",
        "name": "Wrapped Bitcoin",
        "description": "Wrapped Bitcoin is a tokenized version of Bitcoin used on smart contract networks, especially in DeFi applications."
    },
    {
        "old_cgid": "cronos-zkevm-bridged-cro-cronos-zkevm",
        "new_cgid": "cronos",
        "symbol": "cro",
        "name": "Cronos",
        "description": "Cronos is the native token used across the Cronos ecosystem for fees, staking, and platform-related utility."
    },
    {
        "old_cgid": "linea-bridged-uni-linea",
        "new_cgid": "uniswap",
        "symbol": "uni",
        "name": "Uniswap",
        "description": "Uniswap is a decentralized exchange protocol, and UNI is its governance token."
    },
    {
        "old_cgid": "avalanche-bridged-dai-avalanche",
        "new_cgid": "dai",
        "symbol": "dai",
        "name": "Dai",
        "description": "Dai is a decentralized stablecoin designed to stay close to the value of one US dollar."
    },
    {
        "old_cgid": "katana-bridged-pol-katana",
        "new_cgid": "polygon-ecosystem-token",
        "symbol": "pol",
        "name": "POL (ex-MATIC)",
        "description": "POL is the Polygon ecosystem token used in the network's evolving staking, governance, and infrastructure model."
    },
    {
        "old_cgid": "layerzero-bridged-sei",
        "new_cgid": "sei-network",
        "symbol": "sei",
        "name": "Sei",
        "description": "Sei is a blockchain network built for fast trading-oriented and general on-chain applications."
    },
    {
        "old_cgid": "bridged-curve-dao-token-stargate",
        "new_cgid": "curve-dao-token",
        "symbol": "crv",
        "name": "Curve DAO",
        "description": "CRV is the governance token of Curve, a decentralized exchange protocol focused on efficient stable asset trading."
    },
    {
        "old_cgid": "luna-wormhole",
        "new_cgid": "terra-luna-classic",
        "symbol": "lunc",
        "name": "Terra Luna Classic",
        "description": "Terra Luna Classic is the legacy token of the original Terra chain that continues to operate as a separate community-driven network."
    },
    {
        "old_cgid": "eureka-bridged-pax-gold-terra",
        "new_cgid": "pax-gold",
        "symbol": "paxg",
        "name": "PAX Gold",
        "description": "PAX Gold is a token designed to represent ownership of physical gold."
    },
    {
        "old_cgid": "bridged-nxpc",
        "new_cgid": "nexpace",
        "symbol": "nxpc",
        "name": "Nexpace",
        "description": "Nexpace is a crypto token tracked on CoinGecko under the symbol NXPC."
    },
    {
        "old_cgid": "bridged-maga-wormhole",
        "new_cgid": "official-trump",
        "symbol": "trump",
        "name": "Official Trump",
        "description": "Official Trump is a crypto token tracked on CoinGecko under the symbol TRUMP."
    },
]

INSERTS = [
    {
        "cgid": "binancecoin",
        "symbol": "bnb",
        "name": "BNB",
        "description": "BNB is the native token associated with the BNB ecosystem and is used for fees, utility, and participation across related services."
    },
    {
        "cgid": "tron",
        "symbol": "trx",
        "name": "TRON",
        "description": "TRON is a blockchain platform focused on digital content, smart contracts, and token-based ecosystem activity."
    },
]

def main():
    if not os.path.exists(DB_PATH):
        raise FileNotFoundError(f"DB not found: {DB_PATH}")

    backup_path = f"{DB_PATH}.bak-manual-curated-{int(time.time())}"
    shutil.copy2(DB_PATH, backup_path)
    print(f"[backup] created: {backup_path}")

    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    cur = conn.cursor()

    before_version = cur.execute("PRAGMA user_version").fetchone()[0]
    now_ms = int(time.time() * 1000)

    replaced = 0
    inserted = 0
    skipped = 0

    cur.execute("BEGIN")

    try:
        for item in REPLACEMENTS:
            old_row = cur.execute(
                "SELECT cgId, symbol, name FROM tokens WHERE cgId = ?",
                (item["old_cgid"],)
            ).fetchone()

            if not old_row:
                print(f"[skip] missing old row: {item['old_cgid']}")
                skipped += 1
                continue

            target_row = cur.execute(
                "SELECT cgId FROM tokens WHERE cgId = ?",
                (item["new_cgid"],)
            ).fetchone()

            if target_row and item["new_cgid"] != item["old_cgid"]:
                raise RuntimeError(f"Target cgId already exists: {item['new_cgid']}")

            cur.execute(
                """
                UPDATE tokens
                SET cgId = ?, symbol = ?, name = ?, description = ?, imageUrl = NULL, updatedAt = ?
                WHERE cgId = ?
                """,
                (
                    item["new_cgid"],
                    item["symbol"],
                    item["name"],
                    item["description"],
                    now_ms,
                    item["old_cgid"],
                )
            )
            replaced += cur.rowcount

        for item in INSERTS:
            exists = cur.execute(
                "SELECT 1 FROM tokens WHERE cgId = ? OR lower(symbol) = ?",
                (item["cgid"], item["symbol"])
            ).fetchone()

            if exists:
                print(f"[skip] insert exists already: {item['cgid']} / {item['symbol']}")
                skipped += 1
                continue

            cur.execute(
                """
                INSERT INTO tokens (cgId, symbol, name, description, imageUrl, updatedAt)
                VALUES (?, ?, ?, ?, NULL, ?)
                """,
                (
                    item["cgid"],
                    item["symbol"],
                    item["name"],
                    item["description"],
                    now_ms,
                )
            )
            inserted += 1

        after_version = before_version + 1
        cur.execute(f"PRAGMA user_version = {after_version}")
        conn.commit()

    except Exception:
        conn.rollback()
        raise
    finally:
        conn.close()

    print(f"[done] replaced={replaced} inserted={inserted} skipped={skipped}")
    print(f"[done] user_version: {before_version} -> {before_version + 1}")

if __name__ == "__main__":
    main()