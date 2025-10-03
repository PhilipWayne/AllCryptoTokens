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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)   // <-- fixes TopAppBar red underline
@Composable
fun TokenListScreen() {
    val ctx = LocalContext.current
    val dao = remember { AppDatabase.get(ctx).tokenDao() }

    var rows by remember { mutableStateOf<List<TokenEntity>>(emptyList()) }

    // Observe the local database only (works offline)
    LaunchedEffect(Unit) {
        dao.observeAll().collectLatest { rows = it }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("All Crypto Tokens") }) }
    ) { pad ->
        if (rows.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pad)
                    .padding(16.dp)
            ) {
                Text(
                    text = "No tokens in local database.\n" +
                            "If you’re offline, preloading will let this screen work without internet."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pad)
            ) {
                items(rows, key = { it.cgId }) { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Intent(ctx, TokenDetailActivity::class.java).apply {
                                    putExtra("cg_id", row.cgId)
                                    putExtra("symbol", row.symbol)
                                    putExtra("name", row.name)
                                    putExtra("imageUrl", row.imageUrl)
                                }.also(ctx::startActivity)
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        row.imageUrl?.let { url ->
                            Image(
                                painter = rememberAsyncImagePainter(url),
                                contentDescription = "${row.name} logo",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            Text(text = row.name, style = MaterialTheme.typography.bodyLarge)
                            Text(text = row.symbol, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
