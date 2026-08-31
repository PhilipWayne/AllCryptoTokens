package com.allcryptotokens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.allcryptotokens.ui.ads.BannerAd
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

private const val BANNER_AD_UNIT_ID = "ca-app-pub-5252933140466741/1627322069"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenDetailScreen(
    cgId: String
) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.get(context).tokenDao() }
    val marketRepository = remember {
        MarketRepository(NetworkModule.marketService(context))
    }

    val token by dao.observeToken(cgId).collectAsState(initial = null)
    val scroll = rememberScrollState()

    var marketData by remember(cgId) { mutableStateOf<MarketCurrentData?>(null) }
    var marketLoading by remember(cgId) { mutableStateOf(true) }
    var marketUnavailable by remember(cgId) { mutableStateOf(false) }

    LaunchedEffect(cgId) {
        marketLoading = true
        marketUnavailable = false

        try {
            marketData = marketRepository.getCurrent(cgId)
            marketUnavailable = marketData == null
        } catch (_: Exception) {
            marketData = null
            marketUnavailable = true
        } finally {
            marketLoading = false
        }
    }

    val title = token?.let { "${it.name} (${it.symbol.uppercase()})" } ?: cgId
    val desc = token?.description?.takeIf { it.isNotBlank() } ?: "No description yet."
    val youtubeId = token?.youtubeId?.takeIf { it.isNotBlank() }
    val officialUrl = token?.officialUrl?.takeIf { it.isNotBlank() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                BannerAd(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    adUnitId = BANNER_AD_UNIT_ID
                )
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = token?.imageUrl?.takeIf { it.isNotBlank() },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            MarketDataCard(
                marketData = marketData,
                loading = marketLoading,
                unavailable = marketUnavailable,
                modifier = Modifier.fillMaxWidth()
            )

            if (youtubeId != null) {
                YoutubePreviewCard(
                    videoId = youtubeId,
                    title = token?.name ?: "Video",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(text = desc)

            if (officialUrl != null) {
                Button(
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(officialUrl))
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD600),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Go to Website",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun MarketDataCard(
    marketData: MarketCurrentData?,
    loading: Boolean,
    unavailable: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Market Data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            when {
                loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                        Text("Loading market data…")
                    }
                }

                unavailable || marketData == null -> {
                    Text(
                        text = "Market data is currently unavailable.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> {
                    Text(
                        text = formatPrice(marketData.price),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )

                    MarketValueRow(
                        label = "24h change",
                        value = formatPercent(marketData.change24h),
                        valueColor = changeColor(marketData.change24h)
                    )

                    MarketValueRow(
                        label = "Market cap",
                        value = formatCompactUsd(marketData.marketCap)
                    )

                    MarketValueRow(
                        label = "24h volume",
                        value = formatCompactUsd(marketData.volume24h)
                    )

                    MarketValueRow(
                        label = "Rank",
                        value = marketData.rank?.let { "#$it" } ?: "—"
                    )

                    MarketValueRow(
                        label = "All-time high",
                        value = formatPrice(marketData.allTimeHighPrice)
                    )

                    MarketValueRow(
                        label = "ATH date",
                        value = marketData.allTimeHighTimestamp
                            ?.let(::formatDate)
                            ?: "—"
                    )

                    Text(
                        text = "Last updated: ${formatTimestamp(marketData.updatedAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Data provided by Coinranking",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MarketValueRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

private fun formatPrice(value: Double?): String {
    if (value == null || !value.isFinite()) return "—"

    val absolute = abs(value)
    val pattern = when {
        absolute >= 1.0 -> "#,##0.00"
        absolute >= 0.01 -> "0.0000"
        absolute >= 0.0001 -> "0.000000"
        absolute >= 0.00000001 -> "0.00000000"
        absolute == 0.0 -> "0.00"
        else -> return "\$" + String.format(Locale.US, "%.4e", value)
    }

    return "\$" + DecimalFormat(pattern).format(value)
}

private fun formatCompactUsd(value: Double?): String {
    if (value == null || !value.isFinite()) return "—"

    val absolute = abs(value)
    val divisor: Double
    val suffix: String

    when {
        absolute >= 1_000_000_000_000.0 -> {
            divisor = 1_000_000_000_000.0
            suffix = "T"
        }
        absolute >= 1_000_000_000.0 -> {
            divisor = 1_000_000_000.0
            suffix = "B"
        }
        absolute >= 1_000_000.0 -> {
            divisor = 1_000_000.0
            suffix = "M"
        }
        absolute >= 1_000.0 -> {
            divisor = 1_000.0
            suffix = "K"
        }
        else -> {
            val currency = NumberFormat.getCurrencyInstance(Locale.US)
            currency.maximumFractionDigits = 2
            return currency.format(value)
        }
    }

    return "\$" + DecimalFormat("0.##").format(value / divisor) + suffix
}

private fun formatPercent(value: Double?): String {
    if (value == null || !value.isFinite()) return "—"
    val sign = if (value > 0) "+" else ""
    return sign + DecimalFormat("0.00").format(value) + "%"
}

@Composable
private fun changeColor(value: Double?): Color {
    return when {
        value == null -> MaterialTheme.colorScheme.onSurface
        value > 0 -> Color(0xFF4CAF50)
        value < 0 -> Color(0xFFEF5350)
        else -> MaterialTheme.colorScheme.onSurface
    }
}

private fun formatTimestamp(epochSeconds: Long): String {
    val formatter = SimpleDateFormat(
        "MMM d, yyyy, h:mm a",
        Locale.getDefault()
    )
    return formatter.format(Date(epochSeconds * 1000L))
}

private fun formatDate(epochSeconds: Long): String {
    val formatter = SimpleDateFormat(
        "MMM d, yyyy",
        Locale.getDefault()
    )
    return formatter.format(Date(epochSeconds * 1000L))
}

@Composable
private fun YoutubePreviewCard(
    videoId: String,
    title: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val watchUrl = "https://www.youtube.com/watch?v=$videoId"
    val thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

    val openYoutube: () -> Unit = {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(watchUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(appIntent)
        } catch (_: Exception) {
            context.startActivity(webIntent)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF111111))
            .clickable { openYoutube() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.28f))
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.62f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.58f))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Watch on YouTube",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = title,
                color = Color.White.copy(alpha = 0.86f)
            )

            Button(
                onClick = openYoutube,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Open Video")
            }
        }
    }
}
