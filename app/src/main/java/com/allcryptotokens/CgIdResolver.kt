// app/src/main/java/com/allcryptotokens/CgIdResolver.kt
package com.allcryptotokens

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Resolves Crypto.com symbols (e.g., "NEON") to CoinGecko ids (e.g., "neon").
 * - Boots with /coins/list once (fast, cached).
 * - Disambiguates symbols with multiple ids via name similarity or /search (market cap rank).
 * - Persists resolved mappings in SharedPreferences to avoid repeat work.
 */
object CgIdResolver {

    private const val TAG = "CgIdResolver"
    private const val PREFS = "cg_id_cache"
    private const val KEY_MAP = "map_json" // stores { "BTC":"bitcoin", ... }

    // In-memory caches
    private val symbolToId = ConcurrentHashMap<String, String>()
    private var listLoaded = false
    private var symbolToCandidates: Map<String, List<CgCoinListItem>> = emptyMap()

    /** Load cached map from disk into memory. */
    private fun loadFromPrefs(context: Context) {
        if (symbolToId.isNotEmpty()) return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_MAP, null) ?: return
        try {
            val obj = JSONObject(raw)
            obj.keys().forEach { sym ->
                symbolToId[sym] = obj.getString(sym)
            }
            Log.d(TAG, "Loaded ${symbolToId.size} cached mappings")
        } catch (_: Throwable) { /* ignore */ }
    }

    /** Save current map to disk. */
    private fun saveToPrefs(context: Context) {
        val obj = JSONObject()
        symbolToId.forEach { (k, v) -> obj.put(k, v) }
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_MAP, obj.toString()).apply()
    }

    /** Preload CoinGecko coins list once into memory for fast symbol lookups. */
    suspend fun preloadList(context: Context, cg: CoinGeckoApiService) {
        loadFromPrefs(context)
        if (listLoaded) return
        val list = withContext(Dispatchers.IO) { cg.coinsList(includePlatform = false) }
        symbolToCandidates = list.groupBy { it.symbol.uppercase(Locale.US) }
        listLoaded = true
        Log.d(TAG, "Loaded CoinGecko coins list: ${list.size} entries")
    }

    /**
     * Resolve a symbol to a CoinGecko id.
     * @param preferredName optional human name hint from Crypto.com (helps disambiguation)
     */
    suspend fun resolve(
        context: Context,
        cg: CoinGeckoApiService,
        symbol: String,
        preferredName: String? = null
    ): String? = withContext(Dispatchers.IO) {
        val key = symbol.uppercase(Locale.US)
        loadFromPrefs(context)

        // 1) Already cached?
        symbolToId[key]?.let { return@withContext it }

        // 2) Ensure list loaded then filter candidates by symbol
        if (!listLoaded) preloadList(context, cg)
        val candidates = symbolToCandidates[key].orEmpty()
        if (candidates.isEmpty()) {
            // 3) As a last resort: use /search to find the best match by market cap rank
            val searched = try {
                cg.search(symbol).coins
            } catch (_: Exception) {
                emptyList()
            }
            val exact = searched.filter { it.symbol.equals(symbol, true) }
            val byRank = (if (exact.isNotEmpty()) exact else searched)
                .minByOrNull { it.marketCapRank ?: Int.MAX_VALUE }
                ?.id
            byRank?.let {
                symbolToId[key] = it
                saveToPrefs(context)
                Log.d(TAG, "Resolved via search: $symbol -> $it")
                return@withContext it
            }
            Log.d(TAG, "No candidates for symbol=$symbol")
            return@withContext null
        }

        // 3) Try to pick best candidate using name hints
        val nameHint = preferredName?.lowercase(Locale.US)
        val exactName = nameHint?.let { n ->
            candidates.firstOrNull { it.name.equals(n, true) }?.id
        }
        if (exactName != null) {
            symbolToId[key] = exactName
            saveToPrefs(context)
            Log.d(TAG, "Resolved by exact name: $symbol -> $exactName")
            return@withContext exactName
        }

        val containsName = nameHint?.let { n ->
            candidates.firstOrNull { it.name.lowercase(Locale.US).contains(n) }?.id
        }
        if (containsName != null) {
            symbolToId[key] = containsName
            saveToPrefs(context)
            Log.d(TAG, "Resolved by contains name: $symbol -> $containsName")
            return@withContext containsName
        }

        // 4) Fallback: first candidate (deterministic but not always ideal)
        val fallback = candidates.first().id
        symbolToId[key] = fallback
        saveToPrefs(context)
        Log.d(TAG, "Resolved by fallback: $symbol -> $fallback")
        return@withContext fallback
    }
}
