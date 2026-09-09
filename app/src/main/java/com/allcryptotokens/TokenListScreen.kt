package com.allcryptotokens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenListScreen(
    isAdFree: Boolean,
    onOpenToken: (cgId: String) -> Unit,
    onOpenRemoveAds: () -> Unit,
    onOpenAbout: () -> Unit
) {
    val ctx = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val dao = remember { AppDatabase.get(ctx).tokenDao() }

    var query by remember { mutableStateOf(TextFieldValue("")) }

    val flow: Flow<List<TokenEntity>> = remember(query.text) {
        val q = query.text.trim()
        if (q.isEmpty()) dao.observeAllTokens() else dao.searchTokens(q)
    }

    val tokens by flow.collectAsState(initial = emptyList())

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.80f),
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                drawerContainerColor = Color(0xFF1F1F1F)
            ) {
                Column(Modifier.fillMaxHeight().statusBarsPadding().navigationBarsPadding().padding(20.dp)) {
                    Text("AllCryptoTokens", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD600))
                    Spacer(Modifier.height(8.dp))
                    Text("Explore • Learn • Discover", color = Color.LightGray)
                    Spacer(Modifier.height(30.dp))
                    NavigationDrawerItem(
                        label = { Text(if (isAdFree) "Ads Removed" else "Remove Ads") },
                        selected = false,
                        icon = { Icon(Icons.Default.RemoveCircleOutline, null, tint = Color(0xFFFFD600)) },
                        onClick = { scope.launch { drawerState.close() }; onOpenRemoveAds() }
                    )
                    Spacer(Modifier.height(10.dp))
                    NavigationDrawerItem(
                        label = { Text("About") },
                        selected = false,
                        icon = { Icon(Icons.Default.Info, null, tint = Color(0xFFFFD600)) },
                        onClick = { scope.launch { drawerState.close() }; onOpenAbout() }
                    )
                }
            }
        }
    ) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Crypto Tokens") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                }
            )
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
                label = { Text("Search") }
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
