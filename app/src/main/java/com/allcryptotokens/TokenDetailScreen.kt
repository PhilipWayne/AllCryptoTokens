// app/src/main/java/com/allcryptotokens/TokenDetailScreen.kt
package com.allcryptotokens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenDetailScreen(
    cgId: String,
    name: String,
    symbol: String,
    initialImageUrl: String? = null
) {
    val ctx = LocalContext.current.applicationContext
    val scroll = rememberScrollState()
    val service = remember { NetworkModule.coinGeckoService(ctx, debug = false) }
    val repo = remember { OfflineFirstTokenRepository(ctx, service) }

    var ui by remember {
        mutableStateOf<TokenEntity?>(
            TokenEntity(cgId, name, symbol, null, initialImageUrl, 0L)
        )
    }
    var firstLoad by remember { mutableStateOf(true) }

    // Observe DB for live updates
    LaunchedEffect(cgId) {
        repo.observe(cgId).collectLatest { entity ->
            if (entity != null) ui = entity
        }
    }

    // Ensure cached and refresh in background
    LaunchedEffect(cgId) {
        repo.ensureCached(cgId, name, symbol, initialImageUrl)
        firstLoad = false
    }

    Scaffold(topBar = { TopAppBar(title = { Text("$name ($symbol)") }) }) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(scroll)
                .padding(16.dp)
        ) {
            val entity = ui

            if (firstLoad && entity?.description == null) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
            }

            (entity?.imageUrl ?: initialImageUrl)?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = "$name logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            // DB already holds plain text (HTML stripped in repository)
            val bodyText = entity?.description ?: "No description available."
            Text(
                text = bodyText,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 22.sp
            )
        }
    }
}
