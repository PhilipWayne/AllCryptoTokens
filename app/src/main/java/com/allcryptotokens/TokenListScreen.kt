package com.allcryptotokens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenListScreen(api: CryptoComApiService) {
    val ctx = LocalContext.current

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var rows by remember { mutableStateOf<List<TokenRow>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            // 1) Discover base symbols from Crypto.com instruments
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

            // 2) Map base symbols -> CoinGecko id + name via coinsList()
            val cg = NetworkModule.coinGeckoService(debug = true)
            val cgList = cg.coinsList()
            val bySymbol = cgList.groupBy { it.symbol.uppercase(Locale.US) }

            var mapped = baseSymbols.map { sym ->
                val match = bySymbol[sym]?.firstOrNull()
                TokenRow(
                    symbol = sym,
                    name = match?.name ?: sym,
                    cgId = match?.id,
                    imageUrl = null
                )
            }

            // 3) Batch-fetch images via markets(ids=...) and merge into rows
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
                mapped = mapped.map { r ->
                    r.copy(imageUrl = r.cgId?.let { imageMap[it] })
                }
            }

            rows = mapped
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("All Crypto Tokens (Crypto.com)") }) }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(16.dp)) {
            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text(error!!, color = Color.Red)
                else -> LazyColumn {
                    items(rows) { row ->
                        ListItem(
                            leadingContent = {
                                row.imageUrl?.let {
                                    Image(
                                        painter = rememberAsyncImagePainter(it),
                                        contentDescription = "${row.name} logo",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            },
                            headlineContent = { Text("${row.name} (${row.symbol})") },
                            supportingContent = {
                                Text(if (row.cgId != null) "Tap for description" else "Not found on CoinGecko")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = row.cgId != null) {
                                    row.cgId?.let { id ->
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
                        Divider()
                    }
                }
            }
        }
    }
}

data class TokenRow(
    val symbol: String,
    val name: String,
    val cgId: String?,
    val imageUrl: String?
)
