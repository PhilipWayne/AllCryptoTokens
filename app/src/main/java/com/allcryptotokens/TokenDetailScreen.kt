package com.allcryptotokens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.flow.collectLatest

/**
 * DETAIL SCREEN ONLY.
 * Shows a single token from the local Room DB.
 * No networking here.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenDetailScreen(
    cgId: String,
    name: String,
    symbol: String,
    initialImageUrl: String? = null
) {
    val ctx = LocalContext.current
    val dao = remember { AppDatabase.get(ctx).tokenDao() }
    var ui by remember { mutableStateOf<TokenEntity?>(null) }
    var firstLoad by remember { mutableStateOf(true) }

    // Observe local DB — updates will recompose the UI instantly
    LaunchedEffect(cgId) {
        dao.observeToken(cgId).collectLatest { entity ->
            ui = entity ?: TokenEntity(cgId, name, symbol, null, initialImageUrl, 0L)
            firstLoad = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("$name ($symbol)") }) }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
        ) {
            val entity = ui

            if (firstLoad && entity?.description == null) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
            }

            entity?.imageUrl?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "${entity.name} logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            Text(
                text = entity?.description ?: "No description available (offline).",
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 22.sp,
                overflow = TextOverflow.Clip
            )
        }
    }
}
