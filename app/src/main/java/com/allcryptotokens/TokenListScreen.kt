package com.allcryptotokens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.allcryptotokens.ui.ads.BannerAd
import kotlinx.coroutines.flow.Flow

private const val BANNER_AD_UNIT_ID = "ca-app-pub-5252933140466741/1627322069"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenListScreen(
    onOpenToken: (cgId: String) -> Unit,
    onOpenAbout: () -> Unit
) {
    val ctx = LocalContext.current
    val dao = remember { AppDatabase.get(ctx).tokenDao() }

    var query by remember { mutableStateOf(TextFieldValue("")) }

    val flow: Flow<List<TokenEntity>> = remember(query.text) {
        val q = query.text.trim()
        if (q.isEmpty()) dao.observeAllTokens() else dao.searchTokens(q)
    }

    val tokens by flow.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Crypto Tokens") },
                actions = {
                    TextButton(onClick = onOpenAbout) { Text("About") }
                }
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
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                singleLine = true,
                label = { Text("Search (name / symbol)") }
            )

            if (tokens.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tokens found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(tokens, key = { it.cgId }) { t ->
                        TokenRow(
                            token = t,
                            onClick = { onOpenToken(t.cgId) }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
private fun TokenRow(
    token: TokenEntity,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        leadingContent = {
            AsyncImage(
                model = token.imageUrl?.takeIf { it.isNotBlank() },
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        },
        headlineContent = { Text("${token.name} (${token.symbol.uppercase()})") }
        // supportingContent removed: cgId is not user-friendly
    )
}
