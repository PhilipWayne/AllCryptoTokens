-- AllCryptoTokens officialUrl homepage patch
-- Source: official_urls_collected.csv
-- Scope: only rows where source = homepage and currentOfficialUrl was empty
-- Does not overwrite existing officialUrl values such as Juno
-- DB user_version: 27 -> 28

BEGIN TRANSACTION;

-- 0x Protocol (zrx)
UPDATE tokens
SET officialUrl = 'https://www.0xprotocol.org/'
WHERE cgId = '0x'
  AND (officialUrl IS NULL OR officialUrl = '');

-- 1 (one) (one)
UPDATE tokens
SET officialUrl = 'https://x.com/i/communities/1956028849130508619'
WHERE cgId = '1-one'
  AND (officialUrl IS NULL OR officialUrl = '');

-- 1INCH (1inch)
UPDATE tokens
SET officialUrl = 'https://1inch.com/'
WHERE cgId = '1inch'
  AND (officialUrl IS NULL OR officialUrl = '');

-- A Hunters Dream (caw)
UPDATE tokens
SET officialUrl = 'https://caw.is'
WHERE cgId = 'a-hunters-dream'
  AND (officialUrl IS NULL OR officialUrl = '');

-- A New Internet Money Era (anime)
UPDATE tokens
SET officialUrl = 'https://www.animecoin.art/'
WHERE cgId = 'a-new-internet-money-era'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Aave (aave)
UPDATE tokens
SET officialUrl = 'https://app.aave.com/'
WHERE cgId = 'aave'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Aavegotchi (ghst)
UPDATE tokens
SET officialUrl = 'https://www.aavegotchi.com/'
WHERE cgId = 'aavegotchi'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Acala (aca)
UPDATE tokens
SET officialUrl = 'https://acala.network/'
WHERE cgId = 'acala'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Access Protocol (acs)
UPDATE tokens
SET officialUrl = 'https://www.accessprotocol.co/'
WHERE cgId = 'access-protocol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Acet (act)
UPDATE tokens
SET officialUrl = 'https://acet.finance/'
WHERE cgId = 'acet-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Across Protocol (acx)
UPDATE tokens
SET officialUrl = 'https://across.to/'
WHERE cgId = 'across-protocol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- ADI (adi)
UPDATE tokens
SET officialUrl = 'https://token.adi.foundation/'
WHERE cgId = 'adi-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Adventure Gold (agld)
UPDATE tokens
SET officialUrl = 'https://www.lootproject.com/'
WHERE cgId = 'adventure-gold'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Aergo (aergo)
UPDATE tokens
SET officialUrl = 'https://aergo.io'
WHERE cgId = 'aergo'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Aerodrome Finance (aero)
UPDATE tokens
SET officialUrl = 'https://aerodrome.finance/'
WHERE cgId = 'aerodrome-finance'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Aethir (ath)
UPDATE tokens
SET officialUrl = 'https://www.aethir.com/'
WHERE cgId = 'aethir'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Aevo (aevo)
UPDATE tokens
SET officialUrl = 'https://www.aevo.xyz/'
WHERE cgId = 'aevo-exchange'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Agent S (s)
UPDATE tokens
SET officialUrl = 'https://agent-s.site/'
WHERE cgId = 'agent-s'
  AND (officialUrl IS NULL OR officialUrl = '');

-- AgentFun.AI (agentfun)
UPDATE tokens
SET officialUrl = 'https://agentfun.ai/'
WHERE cgId = 'agentfun-ai'
  AND (officialUrl IS NULL OR officialUrl = '');

-- AI Rig Complex (arc)
UPDATE tokens
SET officialUrl = 'https://www.arc.fun/'
WHERE cgId = 'ai-rig-complex'
  AND (officialUrl IS NULL OR officialUrl = '');

-- AIOZ Network (aioz)
UPDATE tokens
SET officialUrl = 'https://aioz.network'
WHERE cgId = 'aioz-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- aixbt (aixbt)
UPDATE tokens
SET officialUrl = 'https://aixbt.tech'
WHERE cgId = 'aixbt'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Akash Network (akt)
UPDATE tokens
SET officialUrl = 'https://akash.network'
WHERE cgId = 'akash-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Alchemix (alcx)
UPDATE tokens
SET officialUrl = 'https://alchemix.fi/'
WHERE cgId = 'alchemix'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Alchemy Pay (ach)
UPDATE tokens
SET officialUrl = 'https://alchemypay.org/'
WHERE cgId = 'alchemy-pay'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Aleo (aleo)
UPDATE tokens
SET officialUrl = 'https://www.aleo.org/'
WHERE cgId = 'aleo'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Artificial Liquid Intelligence (ali)
UPDATE tokens
SET officialUrl = 'https://aiprotocol.info'
WHERE cgId = 'alethea-artificial-liquid-intelligence-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Algorand (algo)
UPDATE tokens
SET officialUrl = 'https://algorand.foundation/'
WHERE cgId = 'algorand'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Alitas (alt)
UPDATE tokens
SET officialUrl = 'https://alitas.tech/'
WHERE cgId = 'alitas'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Amp (amp)
UPDATE tokens
SET officialUrl = 'https://www.amp.xyz'
WHERE cgId = 'amp-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Ampleforth Governance (forth)
UPDATE tokens
SET officialUrl = 'https://www.ampleforth.org/'
WHERE cgId = 'ampleforth-governance-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Ankr Network (ankr)
UPDATE tokens
SET officialUrl = 'https://www.ankr.com/'
WHERE cgId = 'ankr'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Anoma (xan)
UPDATE tokens
SET officialUrl = 'https://anoma.net/'
WHERE cgId = 'anoma'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Ape (ape)
UPDATE tokens
SET officialUrl = 'https://apecommunity1.org/'
WHERE cgId = 'ape-3'
  AND (officialUrl IS NULL OR officialUrl = '');

-- ApeSwap (banana)
UPDATE tokens
SET officialUrl = 'https://ape.bond'
WHERE cgId = 'apeswap-finance'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Api3 (api3)
UPDATE tokens
SET officialUrl = 'https://api3.org/'
WHERE cgId = 'api3'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Aptos (apt)
UPDATE tokens
SET officialUrl = 'https://aptosfoundation.org'
WHERE cgId = 'aptos'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Arbitrum (arb)
UPDATE tokens
SET officialUrl = 'https://arbitrum.io/'
WHERE cgId = 'arbitrum'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Arena-Z (a2z)
UPDATE tokens
SET officialUrl = 'https://arena-z.gg/'
WHERE cgId = 'arena-z'
  AND (officialUrl IS NULL OR officialUrl = '');

-- ARK (ark)
UPDATE tokens
SET officialUrl = 'https://ark.io/'
WHERE cgId = 'ark'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Arkham (arkm)
UPDATE tokens
SET officialUrl = 'http://arkm.com/'
WHERE cgId = 'arkham'
  AND (officialUrl IS NULL OR officialUrl = '');

-- ARPA (arpa)
UPDATE tokens
SET officialUrl = 'https://www.arpanetwork.io/en-US'
WHERE cgId = 'arpa'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Arweave (ar)
UPDATE tokens
SET officialUrl = 'https://arweave.org/'
WHERE cgId = 'arweave'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Astar (astr)
UPDATE tokens
SET officialUrl = 'https://astar.network/'
WHERE cgId = 'astar'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Audius (audio)
UPDATE tokens
SET officialUrl = 'https://audius.co/'
WHERE cgId = 'audius'
  AND (officialUrl IS NULL OR officialUrl = '');

-- IDEX (IDEX)
UPDATE tokens
SET officialUrl = 'https://idex.io/'
WHERE cgId = 'aurora-dao'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Aurora (aurora)
UPDATE tokens
SET officialUrl = 'https://aurora.dev/'
WHERE cgId = 'aurora-near'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Avalanche (avax)
UPDATE tokens
SET officialUrl = 'https://www.avax.network'
WHERE cgId = 'avalanche-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Avantis (avnt)
UPDATE tokens
SET officialUrl = 'https://www.avantisfi.com/'
WHERE cgId = 'avantis'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Axelar (axl)
UPDATE tokens
SET officialUrl = 'https://axelar.network/'
WHERE cgId = 'axelar'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Axie Infinity (axs)
UPDATE tokens
SET officialUrl = 'https://axieinfinity.com/'
WHERE cgId = 'axie-infinity'
  AND (officialUrl IS NULL OR officialUrl = '');

-- BABY PEPE (pepe)
UPDATE tokens
SET officialUrl = 'https://babypepelol.wtf/'
WHERE cgId = 'baby-pepe-5'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Baby Troll (troll)
UPDATE tokens
SET officialUrl = 'https://babytrollsol.io/'
WHERE cgId = 'baby-troll-3'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Bancor Network (bnt)
UPDATE tokens
SET officialUrl = 'https://www.bancor.network/'
WHERE cgId = 'bancor'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Band (band)
UPDATE tokens
SET officialUrl = 'https://bandprotocol.com/'
WHERE cgId = 'band-protocol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Based Bonk (bonk)
UPDATE tokens
SET officialUrl = 'https://www.base-bonk.xyz/'
WHERE cgId = 'based-bonk'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Based Fartcoin (fartcoin)
UPDATE tokens
SET officialUrl = 'https://www.basedfartcoin.com/'
WHERE cgId = 'based-fartcoin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Based Fwog (fwog)
UPDATE tokens
SET officialUrl = 'https://basedfwog.info/'
WHERE cgId = 'based-fwog'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Based Mog Coin (mog)
UPDATE tokens
SET officialUrl = 'https://based-mog.site.xyz/'
WHERE cgId = 'based-mog-coin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- BASED SNEK (snek)
UPDATE tokens
SET officialUrl = 'http://www.basedsnek.info/'
WHERE cgId = 'based-snek'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Based Turbo (turbo)
UPDATE tokens
SET officialUrl = 'https://basedturbo.io/'
WHERE cgId = 'based-turbo'
  AND (officialUrl IS NULL OR officialUrl = '');

-- BaseSafe (safe)
UPDATE tokens
SET officialUrl = 'https://basesafe.org/'
WHERE cgId = 'basesafe'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Basic Attention (bat)
UPDATE tokens
SET officialUrl = 'https://basicattentiontoken.org/'
WHERE cgId = 'basic-attention-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Beefy (bifi)
UPDATE tokens
SET officialUrl = 'https://beefy.com/'
WHERE cgId = 'beefy-finance'
  AND (officialUrl IS NULL OR officialUrl = '');

-- BENQI (qi)
UPDATE tokens
SET officialUrl = 'https://app.benqi.fi/'
WHERE cgId = 'benqi'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Berachain (bera)
UPDATE tokens
SET officialUrl = 'https://berachain.com/'
WHERE cgId = 'berachain-bera'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Biconomy (bico)
UPDATE tokens
SET officialUrl = 'https://www.biconomy.io/'
WHERE cgId = 'biconomy'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Big Dog Fink (bink)
UPDATE tokens
SET officialUrl = 'https://bigdogfink.com/'
WHERE cgId = 'big-dog-fink'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Big Pump (pump)
UPDATE tokens
SET officialUrl = 'https://www.bigpump.org/'
WHERE cgId = 'big-pump'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Big Time (bigtime)
UPDATE tokens
SET officialUrl = 'https://bigtime.gg/'
WHERE cgId = 'big-time'
  AND (officialUrl IS NULL OR officialUrl = '');

-- BNB (bnb)
UPDATE tokens
SET officialUrl = 'https://www.binance.com'
WHERE cgId = 'binancecoin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Bitcoin (btc)
UPDATE tokens
SET officialUrl = 'http://www.bitcoin.org'
WHERE cgId = 'bitcoin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Bitcoin Cash (bch)
UPDATE tokens
SET officialUrl = 'https://bch.info/'
WHERE cgId = 'bitcoin-cash'
  AND (officialUrl IS NULL OR officialUrl = '');

-- bitsCrunch Token (bcut)
UPDATE tokens
SET officialUrl = 'https://bitscrunch.com/'
WHERE cgId = 'bitscrunch-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Bittensor (tao)
UPDATE tokens
SET officialUrl = 'https://bittensor.com/'
WHERE cgId = 'bittensor'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Blast (blast)
UPDATE tokens
SET officialUrl = 'https://blast.io/'
WHERE cgId = 'blast'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Stacks (stx)
UPDATE tokens
SET officialUrl = 'http://stacks.co/'
WHERE cgId = 'blockstack'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Blur (blur)
UPDATE tokens
SET officialUrl = 'https://blur.io/'
WHERE cgId = 'blur'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Boba Cat (boba)
UPDATE tokens
SET officialUrl = 'https://www.bobacat.io/'
WHERE cgId = 'bobacat'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Boson (boson)
UPDATE tokens
SET officialUrl = 'http://bosonprotocol.io'
WHERE cgId = 'boson-protocol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Boundless (zkc)
UPDATE tokens
SET officialUrl = 'https://beboundless.xyz/'
WHERE cgId = 'boundless'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Buy the DIP (dip)
UPDATE tokens
SET officialUrl = 'https://buythedip.fun/'
WHERE cgId = 'buy-the-dip'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Caldera (era)
UPDATE tokens
SET officialUrl = 'https://www.caldera.xyz/'
WHERE cgId = 'caldera'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Capybara Nation (bara)
UPDATE tokens
SET officialUrl = 'https://capybaranation.xyz/'
WHERE cgId = 'capybara-nation'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Cardano (ada)
UPDATE tokens
SET officialUrl = 'https://cardano.org/'
WHERE cgId = 'cardano'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Cartesi (ctsi)
UPDATE tokens
SET officialUrl = 'https://cartesi.io/'
WHERE cgId = 'cartesi'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Casper Network (cspr)
UPDATE tokens
SET officialUrl = 'https://casper.network/'
WHERE cgId = 'casper-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Catizen (cati)
UPDATE tokens
SET officialUrl = 'https://linktr.ee/catizen'
WHERE cgId = 'catizen'
  AND (officialUrl IS NULL OR officialUrl = '');

-- catwifmask (mask)
UPDATE tokens
SET officialUrl = 'https://thecatwifmask.com/'
WHERE cgId = 'catwifmask'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Celer Network (celr)
UPDATE tokens
SET officialUrl = 'https://www.celer.network/'
WHERE cgId = 'celer-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Celestia (tia)
UPDATE tokens
SET officialUrl = 'https://celestia.org/'
WHERE cgId = 'celestia'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Cetus Protocol (cetus)
UPDATE tokens
SET officialUrl = 'https://www.cetus.zone/'
WHERE cgId = 'cetus-protocol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Chainflip (flip)
UPDATE tokens
SET officialUrl = 'https://chainflip.io/'
WHERE cgId = 'chainflip'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Chainlink (link)
UPDATE tokens
SET officialUrl = 'https://chain.link/'
WHERE cgId = 'chainlink'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Chia (xch)
UPDATE tokens
SET officialUrl = 'https://www.chia.net/'
WHERE cgId = 'chia'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Chiliz (chz)
UPDATE tokens
SET officialUrl = 'https://www.chiliz.com/'
WHERE cgId = 'chiliz'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Just a chill guy (chillguy)
UPDATE tokens
SET officialUrl = 'https://www.chillguy.io/'
WHERE cgId = 'chill-guy'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Chromia (chr)
UPDATE tokens
SET officialUrl = 'https://chromia.com/'
WHERE cgId = 'chromaway'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Classic USD (usc)
UPDATE tokens
SET officialUrl = 'https://classicusd.com'
WHERE cgId = 'classic-usd'
  AND (officialUrl IS NULL OR officialUrl = '');

-- CloudAI (cloud)
UPDATE tokens
SET officialUrl = 'https://cloudaionbase.eth.limo/'
WHERE cgId = 'cloudai'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Coin98 (c98)
UPDATE tokens
SET officialUrl = 'https://coin98.com/'
WHERE cgId = 'coin98'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Compound (comp)
UPDATE tokens
SET officialUrl = 'https://compound.finance/'
WHERE cgId = 'compound-governance-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- WalletConnect Token (wct)
UPDATE tokens
SET officialUrl = 'https://walletconnect.network'
WHERE cgId = 'connect-token-wct'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Contentos (cos)
UPDATE tokens
SET officialUrl = 'https://www.contentos.io/'
WHERE cgId = 'contentos'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Convex Finance (cvx)
UPDATE tokens
SET officialUrl = 'https://www.convexfinance.com/'
WHERE cgId = 'convex-finance'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Cookie DAO (cookie)
UPDATE tokens
SET officialUrl = 'https://www.cookie.fun/'
WHERE cgId = 'cookie'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Coq Inu (coq)
UPDATE tokens
SET officialUrl = 'https://www.coqinu.com/'
WHERE cgId = 'coq-inu'
  AND (officialUrl IS NULL OR officialUrl = '');

-- CorgiAI (corgiai)
UPDATE tokens
SET officialUrl = 'https://corgiai.xyz/'
WHERE cgId = 'corgiai'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Cosmos Hub (atom)
UPDATE tokens
SET officialUrl = 'http://cosmos.network/'
WHERE cgId = 'cosmos'
  AND (officialUrl IS NULL OR officialUrl = '');

-- COTI (coti)
UPDATE tokens
SET officialUrl = 'https://coti.io/'
WHERE cgId = 'coti'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Covalent X Token (cxt)
UPDATE tokens
SET officialUrl = 'https://www.covalenthq.com/'
WHERE cgId = 'covalent-x-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Creditcoin (ctc)
UPDATE tokens
SET officialUrl = 'https://creditcoin.org/'
WHERE cgId = 'creditcoin-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Crob Mob (crob)
UPDATE tokens
SET officialUrl = 'https://crobcoin.com/'
WHERE cgId = 'crob-mob'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Cronos ID (croid)
UPDATE tokens
SET officialUrl = 'https://cronosid.xyz/'
WHERE cgId = 'cronos-id'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Cronos zkEVM CRO (zkcro)
UPDATE tokens
SET officialUrl = 'https://cronos-pos.org/'
WHERE cgId = 'cronos-zkevm-cro'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Crypto.com Staked ETH (cdceth)
UPDATE tokens
SET officialUrl = 'https://crypto.com/staking'
WHERE cgId = 'crypto-com-staked-eth'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Crypto.com Staked SOL (cdcsol)
UPDATE tokens
SET officialUrl = 'https://crypto.com/staking'
WHERE cgId = 'crypto-com-staked-sol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Curve DAO (crv)
UPDATE tokens
SET officialUrl = 'https://curve.finance'
WHERE cgId = 'curve-dao-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- CYBER (cyber)
UPDATE tokens
SET officialUrl = 'https://cyber.co/'
WHERE cgId = 'cyberconnect'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Dai (dai)
UPDATE tokens
SET officialUrl = 'https://makerdao.com/'
WHERE cgId = 'dai'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Dar Open Network (d)
UPDATE tokens
SET officialUrl = 'https://dalarnia.com/'
WHERE cgId = 'dar-open-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- DarkCrypto Share (sky)
UPDATE tokens
SET officialUrl = 'https://linktr.ee/DarkCrypto.Finance'
WHERE cgId = 'darkcrypto-share'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Data Ai (data)
UPDATE tokens
SET officialUrl = 'https://www.data-ai.co/'
WHERE cgId = 'data-ai'
  AND (officialUrl IS NULL OR officialUrl = '');

-- deBridge (dbr)
UPDATE tokens
SET officialUrl = 'https://debridge.com'
WHERE cgId = 'debridge'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Decentraland (mana)
UPDATE tokens
SET officialUrl = 'https://decentraland.org/'
WHERE cgId = 'decentraland'
  AND (officialUrl IS NULL OR officialUrl = '');

-- DeepBook (deep)
UPDATE tokens
SET officialUrl = 'https://deepbook.tech/'
WHERE cgId = 'deep'
  AND (officialUrl IS NULL OR officialUrl = '');

-- zkRace (zerc)
UPDATE tokens
SET officialUrl = 'https://www.zkrace.com/'
WHERE cgId = 'derace'
  AND (officialUrl IS NULL OR officialUrl = '');

-- DIA (dia)
UPDATE tokens
SET officialUrl = 'https://diadata.org/'
WHERE cgId = 'dia-data'
  AND (officialUrl IS NULL OR officialUrl = '');

-- DigiByte (dgb)
UPDATE tokens
SET officialUrl = 'https://DigiByte.org'
WHERE cgId = 'digibyte'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Do it for the Vine (vine)
UPDATE tokens
SET officialUrl = 'https://x.com/i/communities/1936494822703124770'
WHERE cgId = 'do-it-for-the-vine'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Dogecoin (doge)
UPDATE tokens
SET officialUrl = 'http://dogecoin.com/'
WHERE cgId = 'dogecoin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Dogelon Mars (elon)
UPDATE tokens
SET officialUrl = 'https://dogelonmars.com/'
WHERE cgId = 'dogelon-mars'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Doggensnout Skeptic (dogs)
UPDATE tokens
SET officialUrl = 'https://www.doggensnoutskeptic.com/'
WHERE cgId = 'doggensnout-skeptic'
  AND (officialUrl IS NULL OR officialUrl = '');

-- dogwifhat (wif)
UPDATE tokens
SET officialUrl = 'https://dogwifcoin.org'
WHERE cgId = 'dogwifcoin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Doodles (dood)
UPDATE tokens
SET officialUrl = 'https://www.doodles.app/'
WHERE cgId = 'doodles'
  AND (officialUrl IS NULL OR officialUrl = '');

-- DoubleZero (2z)
UPDATE tokens
SET officialUrl = 'https://doublezero.xyz/'
WHERE cgId = 'doublezero'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Drift Protocol (drift)
UPDATE tokens
SET officialUrl = 'https://app.drift.trade/'
WHERE cgId = 'drift-protocol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- dYdX (dydx)
UPDATE tokens
SET officialUrl = 'https://dydx.trade'
WHERE cgId = 'dydx-chain'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Dymension (dym)
UPDATE tokens
SET officialUrl = 'https://portal.dymension.xyz/'
WHERE cgId = 'dymension'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Echelon Prime (prime)
UPDATE tokens
SET officialUrl = 'https://echelon.io/'
WHERE cgId = 'echelon-prime'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Eclipse (es)
UPDATE tokens
SET officialUrl = 'https://www.eclipse.xyz/'
WHERE cgId = 'eclipse-3'
  AND (officialUrl IS NULL OR officialUrl = '');

-- EigenCloud (prev. EigenLayer) (eigen)
UPDATE tokens
SET officialUrl = 'https://www.eigencloud.xyz/'
WHERE cgId = 'eigenlayer'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Elastos (ela)
UPDATE tokens
SET officialUrl = 'https://elastos.net/'
WHERE cgId = 'elastos'
  AND (officialUrl IS NULL OR officialUrl = '');

-- MultiversX (egld)
UPDATE tokens
SET officialUrl = 'https://multiversx.com/'
WHERE cgId = 'elrond-erd-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Enjin Coin (enj)
UPDATE tokens
SET officialUrl = 'https://enjin.io/'
WHERE cgId = 'enjincoin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Epic Cash (epic)
UPDATE tokens
SET officialUrl = 'https://epiccash.com/'
WHERE cgId = 'epic-cash'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Ethena (ena)
UPDATE tokens
SET officialUrl = 'https://www.ethena.fi/'
WHERE cgId = 'ethena'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Ether.fi (ethfi)
UPDATE tokens
SET officialUrl = 'https://www.ether.fi/'
WHERE cgId = 'ether-fi'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Ethereum (eth)
UPDATE tokens
SET officialUrl = 'https://www.ethereum.org/'
WHERE cgId = 'ethereum'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Ethereum Classic (etc)
UPDATE tokens
SET officialUrl = 'http://ethereumclassic.org'
WHERE cgId = 'ethereum-classic'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Ethereum Name Service (ens)
UPDATE tokens
SET officialUrl = 'https://ens.domains/'
WHERE cgId = 'ethereum-name-service'
  AND (officialUrl IS NULL OR officialUrl = '');

-- EthereumPoW (ethw)
UPDATE tokens
SET officialUrl = 'https://ethereumpow.org/'
WHERE cgId = 'ethereum-pow-iou'
  AND (officialUrl IS NULL OR officialUrl = '');

-- IQ (iq)
UPDATE tokens
SET officialUrl = 'https://iq.wiki/'
WHERE cgId = 'everipedia'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Famous Fox Federation (foxy)
UPDATE tokens
SET officialUrl = 'https://famousfoxes.com/'
WHERE cgId = 'famous-fox-federation'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Ferro (fer)
UPDATE tokens
SET officialUrl = 'https://ferroprotocol.com/'
WHERE cgId = 'ferro'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Artificial Superintelligence Alliance (fet)
UPDATE tokens
SET officialUrl = 'https://www.superintelligence.io/'
WHERE cgId = 'fetch-ai'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Filecoin (fil)
UPDATE tokens
SET officialUrl = 'https://filecoin.io/'
WHERE cgId = 'filecoin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Finger Monkey (fm)
UPDATE tokens
SET officialUrl = 'https://fingermonkeypls.com/'
WHERE cgId = 'finger-monkey'
  AND (officialUrl IS NULL OR officialUrl = '');

-- First Dog to Buy Crypto (toshi)
UPDATE tokens
SET officialUrl = 'https://www.toshidoge.com/'
WHERE cgId = 'first-dog-to-buy-crypto'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Flare (flr)
UPDATE tokens
SET officialUrl = 'https://flare.network/'
WHERE cgId = 'flare-networks'
  AND (officialUrl IS NULL OR officialUrl = '');

-- FLOKI (floki)
UPDATE tokens
SET officialUrl = 'https://www.floki.com/'
WHERE cgId = 'floki'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Flow (flow)
UPDATE tokens
SET officialUrl = 'https://flow.com'
WHERE cgId = 'flow'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Datamine FLUX (flux)
UPDATE tokens
SET officialUrl = 'http://datamine.network/'
WHERE cgId = 'flux'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Fulcrom (ful)
UPDATE tokens
SET officialUrl = 'https://fulcrom.finance/'
WHERE cgId = 'fulcrom'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Gains Network (gns)
UPDATE tokens
SET officialUrl = 'https://gains.trade/'
WHERE cgId = 'gains-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- GALA (gala)
UPDATE tokens
SET officialUrl = 'https://gala.com/'
WHERE cgId = 'gala'
  AND (officialUrl IS NULL OR officialUrl = '');

-- GameM Token (gmt)
UPDATE tokens
SET officialUrl = 'https://gamem-global.com'
WHERE cgId = 'gamem-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Gas (gas)
UPDATE tokens
SET officialUrl = 'https://neo.org/'
WHERE cgId = 'gas'
  AND (officialUrl IS NULL OR officialUrl = '');

-- GEKKO (gekko)
UPDATE tokens
SET officialUrl = 'https://gekkohq.com/'
WHERE cgId = 'gekko'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Gelato (gel)
UPDATE tokens
SET officialUrl = 'https://www.gelato.network/'
WHERE cgId = 'gelato'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Shadow Token (shdw)
UPDATE tokens
SET officialUrl = 'https://www.shdwdrive.com/'
WHERE cgId = 'genesysgo-shadow'
  AND (officialUrl IS NULL OR officialUrl = '');

-- GMX (gmx)
UPDATE tokens
SET officialUrl = 'https://gmx.io/'
WHERE cgId = 'gmx'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Gnosis (gno)
UPDATE tokens
SET officialUrl = 'https://www.gnosis.io/'
WHERE cgId = 'gnosis'
  AND (officialUrl IS NULL OR officialUrl = '');

-- GOATED (goated)
UPDATE tokens
SET officialUrl = 'https://www.goated.com/'
WHERE cgId = 'goated-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- GodsDotFun (gods)
UPDATE tokens
SET officialUrl = 'https://gods.fun'
WHERE cgId = 'godsdotfun'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Golem (glm)
UPDATE tokens
SET officialUrl = 'https://golem.network/'
WHERE cgId = 'golem'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Grass (grass)
UPDATE tokens
SET officialUrl = 'https://grass.io/'
WHERE cgId = 'grass'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Gunz (gun)
UPDATE tokens
SET officialUrl = 'https://gunbygunz.com/'
WHERE cgId = 'gunz'
  AND (officialUrl IS NULL OR officialUrl = '');

-- H (h)
UPDATE tokens
SET officialUrl = 'https://x.com/HCR_BOT'
WHERE cgId = 'h'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Hamster Kombat (hmstr)
UPDATE tokens
SET officialUrl = 'https://hamsterkombat.io/'
WHERE cgId = 'hamster-kombat'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Hashflow (hft)
UPDATE tokens
SET officialUrl = 'https://hashflow.com/'
WHERE cgId = 'hashflow'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Synthetix (snx)
UPDATE tokens
SET officialUrl = 'https://www.synthetix.io/'
WHERE cgId = 'havven'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Hedera (hbar)
UPDATE tokens
SET officialUrl = 'https://www.hedera.com/'
WHERE cgId = 'hedera-hashgraph'
  AND (officialUrl IS NULL OR officialUrl = '');

-- hehe (hehe)
UPDATE tokens
SET officialUrl = 'https://hehecat.org'
WHERE cgId = 'hehecat'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Heima (HEI)
UPDATE tokens
SET officialUrl = 'https://www.heima.network/'
WHERE cgId = 'heima'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Helium (hnt)
UPDATE tokens
SET officialUrl = 'https://www.helium.com/'
WHERE cgId = 'helium'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Helium Mobile (mobile)
UPDATE tokens
SET officialUrl = 'https://www.helium.com/'
WHERE cgId = 'helium-mobile'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Hemi (hemi)
UPDATE tokens
SET officialUrl = 'https://hemi.xyz/'
WHERE cgId = 'hemi'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Heroes of Mavia (mavia)
UPDATE tokens
SET officialUrl = 'https://mavia.com'
WHERE cgId = 'heroes-of-mavia'
  AND (officialUrl IS NULL OR officialUrl = '');

-- High (high)
UPDATE tokens
SET officialUrl = 'https://yephigh.com'
WHERE cgId = 'high'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Holo (hot)
UPDATE tokens
SET officialUrl = 'https://holo.host/'
WHERE cgId = 'holotoken'
  AND (officialUrl IS NULL OR officialUrl = '');

-- HumidiFi (wet)
UPDATE tokens
SET officialUrl = 'https://humidifi.xyz/'
WHERE cgId = 'humidifi'
  AND (officialUrl IS NULL OR officialUrl = '');

-- ICON (icx)
UPDATE tokens
SET officialUrl = 'https://icon.community/'
WHERE cgId = 'icon'
  AND (officialUrl IS NULL OR officialUrl = '');

-- iExec RLC (rlc)
UPDATE tokens
SET officialUrl = 'http://iex.ec/'
WHERE cgId = 'iexec-rlc'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Ika (ika)
UPDATE tokens
SET officialUrl = 'https://ika.xyz/'
WHERE cgId = 'ika'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Illuvium (ilv)
UPDATE tokens
SET officialUrl = 'https://illuvium.io/'
WHERE cgId = 'illuvium'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Immutable (imx)
UPDATE tokens
SET officialUrl = 'https://imx.community/'
WHERE cgId = 'immutable-x'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Inite (init)
UPDATE tokens
SET officialUrl = 'https://inite.io'
WHERE cgId = 'init'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Injective (inj)
UPDATE tokens
SET officialUrl = 'https://injective.com'
WHERE cgId = 'injective-protocol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Internet Computer (icp)
UPDATE tokens
SET officialUrl = 'https://internetcomputer.org/'
WHERE cgId = 'internet-computer'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Intuition (trust)
UPDATE tokens
SET officialUrl = 'https://www.intuition.systems/'
WHERE cgId = 'intuition'
  AND (officialUrl IS NULL OR officialUrl = '');

-- io.net (io)
UPDATE tokens
SET officialUrl = 'https://io.net'
WHERE cgId = 'io'
  AND (officialUrl IS NULL OR officialUrl = '');

-- IOST (iost)
UPDATE tokens
SET officialUrl = 'http://iost.io/'
WHERE cgId = 'iostoken'
  AND (officialUrl IS NULL OR officialUrl = '');

-- IoTeX (iotx)
UPDATE tokens
SET officialUrl = 'https://iotex.io'
WHERE cgId = 'iotex'
  AND (officialUrl IS NULL OR officialUrl = '');

-- JasmyCoin (jasmy)
UPDATE tokens
SET officialUrl = 'https://www.jasmy.co.jp/'
WHERE cgId = 'jasmycoin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Jito (jto)
UPDATE tokens
SET officialUrl = 'https://www.jito.network/'
WHERE cgId = 'jito-governance-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- JOE (joe)
UPDATE tokens
SET officialUrl = 'https://lfj.gg/avalanche'
WHERE cgId = 'joe'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Jupiter Project (jup)
UPDATE tokens
SET officialUrl = 'https://www.jup.io/'
WHERE cgId = 'jupiter'
  AND (officialUrl IS NULL OR officialUrl = '');

-- KAITO (kaito)
UPDATE tokens
SET officialUrl = 'https://yaps.kaito.ai/'
WHERE cgId = 'kaito'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Kamino (kmno)
UPDATE tokens
SET officialUrl = 'https://kamino.com/'
WHERE cgId = 'kamino'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Karrat (karrat)
UPDATE tokens
SET officialUrl = 'https://www.karratcoin.com/'
WHERE cgId = 'karrat'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Kava (kava)
UPDATE tokens
SET officialUrl = 'https://www.kava.io/'
WHERE cgId = 'kava'
  AND (officialUrl IS NULL OR officialUrl = '');

-- KernelDAO (kernel)
UPDATE tokens
SET officialUrl = 'https://kerneldao.com/'
WHERE cgId = 'kernel-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- King Of Meme (lion)
UPDATE tokens
SET officialUrl = 'https://lion.meme/'
WHERE cgId = 'king-of-meme'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Kite (kite)
UPDATE tokens
SET officialUrl = 'https://www.letsgethai.com'
WHERE cgId = 'kite'
  AND (officialUrl IS NULL OR officialUrl = '');

-- KRYLL (krl)
UPDATE tokens
SET officialUrl = 'https://kryll.io'
WHERE cgId = 'kryll'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Kusama (ksm)
UPDATE tokens
SET officialUrl = 'https://kusama.network/'
WHERE cgId = 'kusama'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Kyber Network Crystal (knc)
UPDATE tokens
SET officialUrl = 'https://kyber.network/'
WHERE cgId = 'kyber-network-crystal'
  AND (officialUrl IS NULL OR officialUrl = '');

-- KYVE Network (kyve)
UPDATE tokens
SET officialUrl = 'https://app.kyve.network/'
WHERE cgId = 'kyve-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Lagrange (la)
UPDATE tokens
SET officialUrl = 'https://www.lagrangefoundation.org'
WHERE cgId = 'lagrange'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Layer3 (l3)
UPDATE tokens
SET officialUrl = 'https://layer3.xyz/'
WHERE cgId = 'layer3'
  AND (officialUrl IS NULL OR officialUrl = '');

-- LayerZero (zro)
UPDATE tokens
SET officialUrl = 'https://layerzero.network/'
WHERE cgId = 'layerzero'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Levva Protocol (lvva)
UPDATE tokens
SET officialUrl = 'https://levva.fi/'
WHERE cgId = 'levva-protocol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Lido DAO (ldo)
UPDATE tokens
SET officialUrl = 'https://stake.lido.fi/'
WHERE cgId = 'lido-dao'
  AND (officialUrl IS NULL OR officialUrl = '');

-- LimeWire (lmwr)
UPDATE tokens
SET officialUrl = 'https://limewire.com/u/home'
WHERE cgId = 'limewire-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Linea (linea)
UPDATE tokens
SET officialUrl = 'https://linea.build/association'
WHERE cgId = 'linea'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Liquid CRO (lcro)
UPDATE tokens
SET officialUrl = 'https://veno.finance/'
WHERE cgId = 'liquid-cro'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Liquity (lqty)
UPDATE tokens
SET officialUrl = 'https://www.liquity.org/'
WHERE cgId = 'liquity'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Lisk (lsk)
UPDATE tokens
SET officialUrl = 'https://lisk.com'
WHERE cgId = 'lisk'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Litecoin (ltc)
UPDATE tokens
SET officialUrl = 'http://litecoin.org'
WHERE cgId = 'litecoin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Literally Me (me)
UPDATE tokens
SET officialUrl = 'https://literallyme.vip/'
WHERE cgId = 'literally-me'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Livepeer (lpt)
UPDATE tokens
SET officialUrl = 'https://livepeer.org/'
WHERE cgId = 'livepeer'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Loaf Token (loaf)
UPDATE tokens
SET officialUrl = 'https://loaftoken.com/'
WHERE cgId = 'loaf-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Loopring (lrc)
UPDATE tokens
SET officialUrl = 'https://loopring.io'
WHERE cgId = 'loopring'
  AND (officialUrl IS NULL OR officialUrl = '');

-- LUKSO (lyx)
UPDATE tokens
SET officialUrl = 'https://www.lukso.network/'
WHERE cgId = 'lukso-token-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Lumia (lumia)
UPDATE tokens
SET officialUrl = 'https://lumia.org/'
WHERE cgId = 'lumia'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Treasure (magic)
UPDATE tokens
SET officialUrl = 'https://treasure.lol/'
WHERE cgId = 'magic'
  AND (officialUrl IS NULL OR officialUrl = '');

-- MANEKI (maneki)
UPDATE tokens
SET officialUrl = 'https://manekineko.world/'
WHERE cgId = 'maneki'
  AND (officialUrl IS NULL OR officialUrl = '');

-- MANTRA (om)
UPDATE tokens
SET officialUrl = 'https://www.mantrachain.io/'
WHERE cgId = 'mantra-dao'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Marinade (mnde)
UPDATE tokens
SET officialUrl = 'https://marinade.finance/'
WHERE cgId = 'marinade'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Marlin (pond)
UPDATE tokens
SET officialUrl = 'https://www.marlin.org/'
WHERE cgId = 'marlin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Measurable Data (mdt)
UPDATE tokens
SET officialUrl = 'https://mdt.io'
WHERE cgId = 'measurable-data-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Medusa (metis)
UPDATE tokens
SET officialUrl = 'https://www.medusametis.com/'
WHERE cgId = 'medusa-metis'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Melania Meme (melania)
UPDATE tokens
SET officialUrl = 'https://melaniameme.com/'
WHERE cgId = 'melania-meme'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Enzyme (mln)
UPDATE tokens
SET officialUrl = 'https://enzyme.finance/'
WHERE cgId = 'melon'
  AND (officialUrl IS NULL OR officialUrl = '');

-- mfercoin (mfer)
UPDATE tokens
SET officialUrl = 'https://mirror.xyz/sartoshi.eth/gTS1jOL9JdfbO2--rTIMiGo5SmovIbxyPR7xIJJCxUo'
WHERE cgId = 'mfercoin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Milady Meme Coin (ladys)
UPDATE tokens
SET officialUrl = 'https://milady.gg'
WHERE cgId = 'milady-meme-coin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- MiL.k (mlk)
UPDATE tokens
SET officialUrl = 'https://milkalliance.io/'
WHERE cgId = 'milk-alliance'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Mina Protocol (mina)
UPDATE tokens
SET officialUrl = 'https://minaprotocol.com/'
WHERE cgId = 'mina-protocol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Minted (mtd)
UPDATE tokens
SET officialUrl = 'https://minted.network/'
WHERE cgId = 'minted'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Mistery (mery)
UPDATE tokens
SET officialUrl = 'https://misteryoncro.io/'
WHERE cgId = 'mistery'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Monad (mon)
UPDATE tokens
SET officialUrl = 'https://www.monad.xyz/'
WHERE cgId = 'monad'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Moonbeam (glmr)
UPDATE tokens
SET officialUrl = 'https://moonbeam.network/'
WHERE cgId = 'moonbeam'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Moonriver (movr)
UPDATE tokens
SET officialUrl = 'https://moonbeam.network/networks/moonriver/'
WHERE cgId = 'moonriver'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Morpho (morpho)
UPDATE tokens
SET officialUrl = 'https://morpho.org/'
WHERE cgId = 'morpho'
  AND (officialUrl IS NULL OR officialUrl = '');

-- MovieBloc (mbl)
UPDATE tokens
SET officialUrl = 'https://www.moviebloc.com/'
WHERE cgId = 'moviebloc'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Nano (xno)
UPDATE tokens
SET officialUrl = 'https://nano.org'
WHERE cgId = 'nano'
  AND (officialUrl IS NULL OR officialUrl = '');

-- NEAR Protocol (near)
UPDATE tokens
SET officialUrl = 'https://near.org/'
WHERE cgId = 'near'
  AND (officialUrl IS NULL OR officialUrl = '');

-- NEO (neo)
UPDATE tokens
SET officialUrl = 'https://neo.org/'
WHERE cgId = 'neo'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Neon (neon)
UPDATE tokens
SET officialUrl = 'https://neonevm.org/'
WHERE cgId = 'neon'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Nervos Network (ckb)
UPDATE tokens
SET officialUrl = 'http://nervos.org'
WHERE cgId = 'nervos-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Neuron (nrn)
UPDATE tokens
SET officialUrl = 'https://nrnagents.ai'
WHERE cgId = 'neuron'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Neutron (ntrn)
UPDATE tokens
SET officialUrl = 'https://neutron.org/'
WHERE cgId = 'neutron-3'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Nexpace (NXPC)
UPDATE tokens
SET officialUrl = 'https://msu.io/'
WHERE cgId = 'nexpace'
  AND (officialUrl IS NULL OR officialUrl = '');

-- NKN (nkn)
UPDATE tokens
SET officialUrl = 'https://www.nkn.org/'
WHERE cgId = 'nkn'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Nomad (nom)
UPDATE tokens
SET officialUrl = 'https://warpcast.com/~/channel/nomadicframe'
WHERE cgId = 'nomad'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Nosana (nos)
UPDATE tokens
SET officialUrl = 'https://nosana.io/'
WHERE cgId = 'nosana'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Notcoin (not)
UPDATE tokens
SET officialUrl = 'https://t.me/notcoin_bot'
WHERE cgId = 'notcoin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Numbers Protocol (num)
UPDATE tokens
SET officialUrl = 'https://www.numbersprotocol.io/'
WHERE cgId = 'numbers-protocol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Numeraire (nmr)
UPDATE tokens
SET officialUrl = 'https://numer.ai/'
WHERE cgId = 'numeraire'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Oasys (oas)
UPDATE tokens
SET officialUrl = 'https://www.oasys.games/'
WHERE cgId = 'oasys'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Official Trump (TRUMP)
UPDATE tokens
SET officialUrl = 'https://gettrumpmemes.com/'
WHERE cgId = 'official-trump'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Ondo (ondo)
UPDATE tokens
SET officialUrl = 'https://ondo.foundation/'
WHERE cgId = 'ondo-finance'
  AND (officialUrl IS NULL OR officialUrl = '');

-- One Path (op)
UPDATE tokens
SET officialUrl = 'https://one-path.io/'
WHERE cgId = 'one-path'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Ontology Gas (ong)
UPDATE tokens
SET officialUrl = 'https://ont.io/'
WHERE cgId = 'ong'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Ontology (ont)
UPDATE tokens
SET officialUrl = 'https://ont.io/'
WHERE cgId = 'ontology'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Optimism (op)
UPDATE tokens
SET officialUrl = 'https://www.optimism.io'
WHERE cgId = 'optimism'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Oraichain (orai)
UPDATE tokens
SET officialUrl = 'https://orai.io/'
WHERE cgId = 'oraichain-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Orca (orca)
UPDATE tokens
SET officialUrl = 'https://www.orca.so/'
WHERE cgId = 'orca'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Orchid Protocol (oxt)
UPDATE tokens
SET officialUrl = 'https://www.orchid.com/'
WHERE cgId = 'orchid-protocol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- ORDER (order)
UPDATE tokens
SET officialUrl = 'https://pyramidliquidity.com/'
WHERE cgId = 'order-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Origin Token (ogn)
UPDATE tokens
SET officialUrl = 'http://www.originprotocol.com'
WHERE cgId = 'origin-protocol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- OriginTrail (trac)
UPDATE tokens
SET officialUrl = 'https://origintrail.io/'
WHERE cgId = 'origintrail'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Osmosis (osmo)
UPDATE tokens
SET officialUrl = 'https://osmosis.zone/'
WHERE cgId = 'osmosis'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Parcl (prcl)
UPDATE tokens
SET officialUrl = 'https://www.parcl.co'
WHERE cgId = 'parcl'
  AND (officialUrl IS NULL OR officialUrl = '');

-- PAX Gold (paxg)
UPDATE tokens
SET officialUrl = 'https://www.paxos.com/paxgold/'
WHERE cgId = 'pax-gold'
  AND (officialUrl IS NULL OR officialUrl = '');

-- PayPal USD (pyusd)
UPDATE tokens
SET officialUrl = 'https://www.paypal.com/us/digital-wallet/manage-money/crypto/pyusd'
WHERE cgId = 'paypal-usd'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Peanut (pnut)
UPDATE tokens
SET officialUrl = 'https://peanut-token.xyz'
WHERE cgId = 'peanut-3'
  AND (officialUrl IS NULL OR officialUrl = '');

-- peaq (peaq)
UPDATE tokens
SET officialUrl = 'https://www.peaq.xyz/'
WHERE cgId = 'peaq-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Pendle (pendle)
UPDATE tokens
SET officialUrl = 'https://pendle.finance/'
WHERE cgId = 'pendle'
  AND (officialUrl IS NULL OR officialUrl = '');

-- PirateCash (pirate)
UPDATE tokens
SET officialUrl = 'https://p.cash'
WHERE cgId = 'piratecash'
  AND (officialUrl IS NULL OR officialUrl = '');

-- PIXEL BATTLE (pixel)
UPDATE tokens
SET officialUrl = 'https://www.pixelbattle.world/'
WHERE cgId = 'pixel-battle-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Plasma (xpl)
UPDATE tokens
SET officialUrl = 'https://www.plasma.to/'
WHERE cgId = 'plasma'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Plume (plume)
UPDATE tokens
SET officialUrl = 'https://plume.org/'
WHERE cgId = 'plume'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Polkadot (dot)
UPDATE tokens
SET officialUrl = 'https://polkadot.com/'
WHERE cgId = 'polkadot'
  AND (officialUrl IS NULL OR officialUrl = '');

-- POL (ex-MATIC) (pol)
UPDATE tokens
SET officialUrl = 'https://polygon.technology/'
WHERE cgId = 'polygon-ecosystem-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Polyhedra Network (zkj)
UPDATE tokens
SET officialUrl = 'https://polyhedra.network/'
WHERE cgId = 'polyhedra-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Polymesh (polyx)
UPDATE tokens
SET officialUrl = 'https://polymesh.network/'
WHERE cgId = 'polymesh'
  AND (officialUrl IS NULL OR officialUrl = '');

-- PONKE (ponke)
UPDATE tokens
SET officialUrl = 'https://www.ponke.xyz/'
WHERE cgId = 'ponke'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Popcat (popcat)
UPDATE tokens
SET officialUrl = 'https://popcatsolana.xyz/'
WHERE cgId = 'popcat'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Powerledger (powr)
UPDATE tokens
SET officialUrl = 'https://powerledger.io/'
WHERE cgId = 'power-ledger'
  AND (officialUrl IS NULL OR officialUrl = '');

-- PromptBidder (prompt)
UPDATE tokens
SET officialUrl = 'https://promptbidder.com/'
WHERE cgId = 'promptbidder'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Pyth Network (pyth)
UPDATE tokens
SET officialUrl = 'https://pyth.network/'
WHERE cgId = 'pyth-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Qtum (qtum)
UPDATE tokens
SET officialUrl = 'https://qtum.org/en/'
WHERE cgId = 'qtum'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Quant (qnt)
UPDATE tokens
SET officialUrl = 'https://www.quant.network'
WHERE cgId = 'quant-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Quickswap [OLD] (quick)
UPDATE tokens
SET officialUrl = 'https://quickswap.exchange/'
WHERE cgId = 'quick'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Radworks (rad)
UPDATE tokens
SET officialUrl = 'https://radworks.org/'
WHERE cgId = 'radicle'
  AND (officialUrl IS NULL OR officialUrl = '');

-- RARI (rari)
UPDATE tokens
SET officialUrl = 'https://rari.foundation/'
WHERE cgId = 'rarible'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Ravencoin (rvn)
UPDATE tokens
SET officialUrl = 'https://ravencoin.org/'
WHERE cgId = 'ravencoin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Raydium (ray)
UPDATE tokens
SET officialUrl = 'https://raydium.io/'
WHERE cgId = 'raydium'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Reactive Network (react)
UPDATE tokens
SET officialUrl = 'https://reactive.network/'
WHERE cgId = 'reactive-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- RedStone (red)
UPDATE tokens
SET officialUrl = 'https://www.redstone.finance/'
WHERE cgId = 'redstone-oracles'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Render (render)
UPDATE tokens
SET officialUrl = 'http://renderfoundation.com'
WHERE cgId = 'render-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Request (req)
UPDATE tokens
SET officialUrl = 'https://request.network'
WHERE cgId = 'request-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Reserve Rights (rsr)
UPDATE tokens
SET officialUrl = 'https://reserve.org/'
WHERE cgId = 'reserve-rights-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- XRP (xrp)
UPDATE tokens
SET officialUrl = 'https://ripple.com/currency/'
WHERE cgId = 'ripple'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Rocket Pool (rpl)
UPDATE tokens
SET officialUrl = 'https://www.rocketpool.net'
WHERE cgId = 'rocket-pool'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Sapien (sapien)
UPDATE tokens
SET officialUrl = 'https://www.sapien.io/'
WHERE cgId = 'sapien-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Sei (sei)
UPDATE tokens
SET officialUrl = 'https://www.sei.io'
WHERE cgId = 'sei-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Shiba Inu (shib)
UPDATE tokens
SET officialUrl = 'https://shibatoken.com/'
WHERE cgId = 'shiba-inu'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Shiden Network (sdn)
UPDATE tokens
SET officialUrl = 'https://shiden.astar.network/'
WHERE cgId = 'shiden'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Shrapnel (shrap)
UPDATE tokens
SET officialUrl = 'https://www.shrapnel.com/'
WHERE cgId = 'shrapnel-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- SKALE (skl)
UPDATE tokens
SET officialUrl = 'https://skale.space/'
WHERE cgId = 'skale'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Solana (sol)
UPDATE tokens
SET officialUrl = 'https://solana.com/'
WHERE cgId = 'solana'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Solayer (layer)
UPDATE tokens
SET officialUrl = 'https://solayer.org/network'
WHERE cgId = 'solayer'
  AND (officialUrl IS NULL OR officialUrl = '');

-- SophiaVerse (soph)
UPDATE tokens
SET officialUrl = 'https://sophiaverse.ai'
WHERE cgId = 'sophiaverse'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Space and Time (sxt)
UPDATE tokens
SET officialUrl = 'https://www.spaceandtime.io'
WHERE cgId = 'space-and-time'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Spark (spk)
UPDATE tokens
SET officialUrl = 'https://spark.fi/'
WHERE cgId = 'spark-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Sparklife (sps)
UPDATE tokens
SET officialUrl = 'https://sparklifesps.com/'
WHERE cgId = 'sparklife'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Spectral (spec)
UPDATE tokens
SET officialUrl = 'https://www.spectrallabs.xyz/'
WHERE cgId = 'spectral'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Spell (spell)
UPDATE tokens
SET officialUrl = 'https://abracadabra.money/'
WHERE cgId = 'spell-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- SPX6900 (spx)
UPDATE tokens
SET officialUrl = 'https://www.spx6900.com'
WHERE cgId = 'spx6900'
  AND (officialUrl IS NULL OR officialUrl = '');

-- ​​Stable (stable)
UPDATE tokens
SET officialUrl = 'https://www.stable.xyz/'
WHERE cgId = 'stable-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Stader (sd)
UPDATE tokens
SET officialUrl = 'https://staderlabs.com/'
WHERE cgId = 'stader'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Stargate Finance (stg)
UPDATE tokens
SET officialUrl = 'https://stargate.finance/'
WHERE cgId = 'stargate-finance'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Starknet (strk)
UPDATE tokens
SET officialUrl = 'https://starknet.io/'
WHERE cgId = 'starknet'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Stellar (xlm)
UPDATE tokens
SET officialUrl = 'https://www.stellar.org/'
WHERE cgId = 'stellar'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Step App (fitfi)
UPDATE tokens
SET officialUrl = 'https://step.app/'
WHERE cgId = 'step-app-fitfi'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Storj (storj)
UPDATE tokens
SET officialUrl = 'https://storj.io'
WHERE cgId = 'storj'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Story (ip)
UPDATE tokens
SET officialUrl = 'https://www.story.foundation/'
WHERE cgId = 'story-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Xertra (strax)
UPDATE tokens
SET officialUrl = 'http://xertra.com/'
WHERE cgId = 'stratis'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Stupid World Liberty Financial (wlfi)
UPDATE tokens
SET officialUrl = 'https://wlfisol.wtf/'
WHERE cgId = 'stupid-world-liberty-financial'
  AND (officialUrl IS NULL OR officialUrl = '');

-- SubQuery Network (sqt)
UPDATE tokens
SET officialUrl = 'https://www.subquery.network/'
WHERE cgId = 'subquery-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Succinct (prove)
UPDATE tokens
SET officialUrl = 'https://www.succinct.xyz/'
WHERE cgId = 'succinct'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Sui (sui)
UPDATE tokens
SET officialUrl = 'https://sui.io/'
WHERE cgId = 'sui'
  AND (officialUrl IS NULL OR officialUrl = '');

-- SuperRare (rare)
UPDATE tokens
SET officialUrl = 'https://superrare.com/'
WHERE cgId = 'superrare'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Sushi (sushi)
UPDATE tokens
SET officialUrl = 'https://sushi.com/'
WHERE cgId = 'sushi'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Synapse (syn)
UPDATE tokens
SET officialUrl = 'https://synapseprotocol.com'
WHERE cgId = 'synapse-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Maple Finance (syrup)
UPDATE tokens
SET officialUrl = 'https://maple.finance/'
WHERE cgId = 'syrup'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Talos (t)
UPDATE tokens
SET officialUrl = 'https://talos.is/'
WHERE cgId = 'talos-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Tectonic (tonic)
UPDATE tokens
SET officialUrl = 'https://tectonic.finance/'
WHERE cgId = 'tectonic'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Tellor Tributes (trb)
UPDATE tokens
SET officialUrl = 'http://www.tellor.io/'
WHERE cgId = 'tellor'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Tensor (tnsr)
UPDATE tokens
SET officialUrl = 'https://www.tensor.foundation'
WHERE cgId = 'tensor'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Tether (usdt)
UPDATE tokens
SET officialUrl = 'https://tether.to/'
WHERE cgId = 'tether'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Tether Gold (xaut)
UPDATE tokens
SET officialUrl = 'https://gold.tether.to/'
WHERE cgId = 'tether-gold'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Tevaera (teva)
UPDATE tokens
SET officialUrl = 'https://tevaera.com/'
WHERE cgId = 'tevaera'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Tezos (xtz)
UPDATE tokens
SET officialUrl = 'https://www.tezos.com/'
WHERE cgId = 'tezos'
  AND (officialUrl IS NULL OR officialUrl = '');

-- The Graph (grt)
UPDATE tokens
SET officialUrl = 'https://thegraph.com/'
WHERE cgId = 'the-graph'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Toncoin (ton)
UPDATE tokens
SET officialUrl = 'https://ton.org/'
WHERE cgId = 'the-open-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- The Sandbox (sand)
UPDATE tokens
SET officialUrl = 'https://www.sandbox.game/en/'
WHERE cgId = 'the-sandbox'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Theta Fuel (tfuel)
UPDATE tokens
SET officialUrl = 'https://www.thetatoken.org/'
WHERE cgId = 'theta-fuel'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Theta Network (theta)
UPDATE tokens
SET officialUrl = 'https://www.thetatoken.org/'
WHERE cgId = 'theta-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- THORChain (rune)
UPDATE tokens
SET officialUrl = 'https://thorchain.org/'
WHERE cgId = 'thorchain'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Towns (towns)
UPDATE tokens
SET officialUrl = 'https://www.towns.com/'
WHERE cgId = 'towns'
  AND (officialUrl IS NULL OR officialUrl = '');

-- TRON (TRX)
UPDATE tokens
SET officialUrl = 'https://tron.network'
WHERE cgId = 'tron'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Truebit Protocol (tru)
UPDATE tokens
SET officialUrl = 'https://truebit.io/'
WHERE cgId = 'truebit-protocol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- UMA (uma)
UPDATE tokens
SET officialUrl = 'https://uma.xyz/'
WHERE cgId = 'uma'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Uniswap (uni)
UPDATE tokens
SET officialUrl = 'https://uniswap.org/'
WHERE cgId = 'uniswap'
  AND (officialUrl IS NULL OR officialUrl = '');

-- USD1 [Old] (usd1)
UPDATE tokens
SET officialUrl = 'https://kinesis.money/pro/usd1/'
WHERE cgId = 'usd1'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Useless Coin (useless)
UPDATE tokens
SET officialUrl = 'https://letsbonk.fun/token/Dz9mQ9NzkBcCsuGPFJ3r1bS4wgqKMHBPiVuniW8Mbonk'
WHERE cgId = 'useless-3'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Vanar Chain (vanry)
UPDATE tokens
SET officialUrl = 'https://vanarchain.com'
WHERE cgId = 'vanar-chain'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Vara Network (vara)
UPDATE tokens
SET officialUrl = 'https://start.vara.network'
WHERE cgId = 'vara-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Vaulta (a)
UPDATE tokens
SET officialUrl = 'https://www.vaulta.com'
WHERE cgId = 'vaulta'
  AND (officialUrl IS NULL OR officialUrl = '');

-- VeChain (vet)
UPDATE tokens
SET officialUrl = 'https://www.vechain.org'
WHERE cgId = 'vechain'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Velo (velo)
UPDATE tokens
SET officialUrl = 'https://velo.org/'
WHERE cgId = 'velo'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Veno Finance (vno)
UPDATE tokens
SET officialUrl = 'https://veno.finance/'
WHERE cgId = 'veno-finance'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Verasity (vra)
UPDATE tokens
SET officialUrl = 'https://verasity.io/'
WHERE cgId = 'verasity'
  AND (officialUrl IS NULL OR officialUrl = '');

-- VeThor (vtho)
UPDATE tokens
SET officialUrl = 'https://www.vechain.org/'
WHERE cgId = 'vethor-token'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Victorious (w)
UPDATE tokens
SET officialUrl = 'https://buyvictorious.com/'
WHERE cgId = 'victorious'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Virtuals Protocol (virtual)
UPDATE tokens
SET officialUrl = 'https://www.virtuals.io/'
WHERE cgId = 'virtual-protocol'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Voxies (voxel)
UPDATE tokens
SET officialUrl = 'https://voxies.io/'
WHERE cgId = 'voxies'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Vulcan Forged (pyr)
UPDATE tokens
SET officialUrl = 'https://vulcanforged.com/'
WHERE cgId = 'vulcan-forged'
  AND (officialUrl IS NULL OR officialUrl = '');

-- VVS Finance (vvs)
UPDATE tokens
SET officialUrl = 'https://vvs.finance/'
WHERE cgId = 'vvs-finance'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Walrus (wal)
UPDATE tokens
SET officialUrl = 'https://www.walrus.xyz/'
WHERE cgId = 'walrus-2'
  AND (officialUrl IS NULL OR officialUrl = '');

-- WAX (waxp)
UPDATE tokens
SET officialUrl = 'https://wax.io/'
WHERE cgId = 'wax'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Wen (wen)
UPDATE tokens
SET officialUrl = 'https://www.wenwencoin.com/'
WHERE cgId = 'wen-4'
  AND (officialUrl IS NULL OR officialUrl = '');

-- WOO (woo)
UPDATE tokens
SET officialUrl = 'https://woo.org'
WHERE cgId = 'woo-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Worldcoin (wld)
UPDATE tokens
SET officialUrl = 'https://worldcoin.org/'
WHERE cgId = 'worldcoin-wld'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Wrapped Bitcoin (wbtc)
UPDATE tokens
SET officialUrl = 'https://www.wbtc.network/'
WHERE cgId = 'wrapped-bitcoin'
  AND (officialUrl IS NULL OR officialUrl = '');

-- XYO Network (xyo)
UPDATE tokens
SET officialUrl = 'https://xyo.network/'
WHERE cgId = 'xyo-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- yearn.finance (yfi)
UPDATE tokens
SET officialUrl = 'https://yearn.fi/'
WHERE cgId = 'yearn-finance'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Yield Guild Games (ygg)
UPDATE tokens
SET officialUrl = 'https://yieldguild.io/'
WHERE cgId = 'yield-guild-games'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Zebec Network (zbcn)
UPDATE tokens
SET officialUrl = 'https://zebec.io/'
WHERE cgId = 'zebec-network'
  AND (officialUrl IS NULL OR officialUrl = '');

-- ZED RUN (zed)
UPDATE tokens
SET officialUrl = 'https://zedchampions.com/'
WHERE cgId = 'zed-run'
  AND (officialUrl IS NULL OR officialUrl = '');

-- ZetaChain (zeta)
UPDATE tokens
SET officialUrl = 'https://www.zetachain.com/'
WHERE cgId = 'zetachain'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Zilliqa (zil)
UPDATE tokens
SET officialUrl = 'https://www.zilliqa.com/'
WHERE cgId = 'zilliqa'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Zircuit (zrc)
UPDATE tokens
SET officialUrl = 'https://www.zircuit.com/'
WHERE cgId = 'zircuit'
  AND (officialUrl IS NULL OR officialUrl = '');

-- ZKsync (zk)
UPDATE tokens
SET officialUrl = 'https://zksync.io/'
WHERE cgId = 'zksync'
  AND (officialUrl IS NULL OR officialUrl = '');

-- Zora (zora)
UPDATE tokens
SET officialUrl = 'https://zora.co/'
WHERE cgId = 'zora'
  AND (officialUrl IS NULL OR officialUrl = '');

PRAGMA user_version = 28;

COMMIT;

-- Verification:
-- PRAGMA user_version;
-- SELECT COUNT(*) FROM tokens WHERE officialUrl IS NOT NULL AND officialUrl != '';
-- SELECT cgId, name, officialUrl FROM tokens WHERE officialUrl IS NULL OR officialUrl = '' ORDER BY name;