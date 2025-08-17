// app/src/main/java/com/allcryptotokens/TokenListScreen.kt
package com.allcryptotokens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenListScreen(api: CryptoComApiService) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val cg = remember { NetworkModule.coinGeckoService(ctx, debug = false) } // CoinGecko (context-aware)
    val repo = remember { OfflineFirstTokenRepository(context = ctx, api = cg) }

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var rows by remember { mutableStateOf<List<TokenRow>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            // 1) Get instruments from Crypto.com
            val instEnv = api.getInstruments()
            val all = instEnv.result?.data.orEmpty()
                .ifEmpty { instEnv.result?.instruments.orEmpty() }

            val spotLike = all.filter {
                val t = it.instrument_type?.uppercase(Locale.US) ?: ""
                t == "CCY_PAIR" || t == "SPOT"
            }

            val baseSymbols = spotLike
                .mapNotNull { it.base_currency?.uppercase(Locale.US) }
                .distinct()
                .sorted()

            if (baseSymbols.isEmpty()) {
                error = "No base symbols discovered from Crypto.com."
                isLoading = false
                return@LaunchedEffect
            }

            // 2) Map base symbols → CoinGecko ids + names via /coins/list (fast, no per-symbol calls)
            val cgList = cg.coinsList()
            val bySymbol = cgList.groupBy { it.symbol.uppercase(Locale.US) }

            var mapped = baseSymbols.map { sym ->
                val match = bySymbol[sym]?.firstOrNull()
                TokenRow(
                    symbol = sym,
                    name = match?.name ?: sym,
                    cgId = match?.id, // may still be null/ambiguous; we resolve on tap if needed
                    imageUrl = null
                )
            }

            // 3) Fetch icons via /markets?ids=...
            val ids = mapped.mapNotNull { it.cgId }
            if (ids.isNotEmpty()) {
                val imageMap = buildMap<String, String?> {
                    ids.chunked(200).forEach { chunk ->
                        val markets = cg.markets(
                            vsCurrency = "usd",
                            order = "market_cap_desc",
                            perPage = 250,
                            page = 1,
                            sparkline = false,
                            ids = chunk.joinToString(",")
                        )
                        markets.forEach { put(it.id, it.image) }
                    }
                }
                mapped = mapped.map { r -> r.copy(imageUrl = r.cgId?.let { imageMap[it] }) }
            }

            rows = mapped
        } catch (e: HttpException) {
            error = if (e.code() == 429) "Rate limit reached (429). Try again in a bit."
            else "HTTP ${e.code()}: ${e.message()}"
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Prewarm cache for a handful so details are instant (ONLY those that already have an id)
    LaunchedEffect(rows) {
        rows.take(40).forEach { r ->
            val id = r.cgId ?: return@forEach
            repo.ensureCached(id, r.name, r.symbol, r.imageUrl)
            delay(120) // stay friendly to APIs
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("All Crypto Tokens (Crypto.com)") }) }) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
                else -> LazyColumn {
                    items(rows, key = { it.symbol }) { row ->
                        ListItem(
                            leadingContent = {
                                row.imageUrl?.let { url ->
                                    Image(
                                        painter = rememberAsyncImagePainter(url),
                                        contentDescription = "${row.name} logo",
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            },
                            headlineContent = { Text("${row.name} (${row.symbol})") },
                            supportingContent = {
                                Text(if (row.cgId != null) "Tap for description" else "Tap to resolve & view")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        // If we don't have an id yet, resolve it via /search (best by market-cap rank)
                                        val id = row.cgId ?: resolveCgId(cg, row.symbol, row.name)
                                        if (id == null) {
                                            Toast
                                                .makeText(ctx, "Could not resolve ${row.symbol} on CoinGecko", Toast.LENGTH_SHORT)
                                                .show()
                                            return@launch
                                        }

                                        // Warm cache then navigate
                                        repo.ensureCached(id, row.name, row.symbol, row.imageUrl)

                                        ctx.startActivity(
                                            Intent(ctx, TokenDetailActivity::class.java).apply {
                                                putExtra("cg_id", id)
                                                putExtra("symbol", row.symbol)
                                                putExtra("name", row.name)
                                                putExtra("imageUrl", row.imageUrl)
                                            }
                                        )
                                    }
                                }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

/** Row model for the list */
data class TokenRow(
    val symbol: String,
    val name: String,
    val cgId: String?,
    val imageUrl: String?
)

/**
 * Resolve a symbol to a CoinGecko id using the /search endpoint:
 * - Prefer exact symbol matches (case-insensitive)
 * - Pick the one with the best (lowest) market_cap_rank
 * Returns null if not found or on error.
 */
private suspend fun resolveCgId(
    cg: CoinGeckoApiService,
    symbol: String,
    nameHint: String?
): String? {
    return try {
        val res = cg.search(symbol)
        val exact = res.coins.filter { it.symbol.equals(symbol, ignoreCase = true) }

        // If we have a name hint, prefer coins whose name matches/contains it
        val candidates = if (exact.isNotEmpty()) exact else res.coins
        val withNameBoost = nameHint?.let { hint ->
            val h = hint.trim().lowercase()
            val exactName = candidates.firstOrNull { it.name.equals(hint, ignoreCase = true) }
            exactName?.let { listOf(it) }
                ?: candidates.filter { it.name.lowercase().contains(h) }.ifEmpty { candidates }
        } ?: candidates

        // Pick by best (lowest) market cap rank
        withNameBoost.minByOrNull { it.marketCapRank ?: Int.MAX_VALUE }?.id
    } catch (_: Exception) {
        null
    }
}

