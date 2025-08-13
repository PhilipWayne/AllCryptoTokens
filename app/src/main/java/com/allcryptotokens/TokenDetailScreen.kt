package com.allcryptotokens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenDetailScreen(
    cgId: String,
    name: String,
    symbol: String,
    initialImageUrl: String? = null
) {
    var isLoading by remember { mutableStateOf(true) }
    var desc by remember { mutableStateOf<String?>(null) }
    var img by remember { mutableStateOf<String?>(initialImageUrl) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(cgId) {
        try {
            val cg = NetworkModule.coinGeckoService(debug = true)
            val d = cg.coinDetail(cgId)
            desc = d.description?.en?.let { stripHtml(it) }?.ifBlank { null }
            img = img ?: d.image?.large ?: d.image?.small ?: d.image?.thumb
        } catch (e: Exception) {
            error = "Failed to load details: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("$name ($symbol)") }) }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).fillMaxSize()) {
            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text(error ?: "", color = MaterialTheme.colorScheme.error)
                else -> {
                    img?.let {
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = "$name logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    Text(
                        text = desc ?: "No description available.",
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun stripHtml(src: String): String =
    src.replace(Regex("<[^>]*>"), "").replace("&amp;", "&").trim()
