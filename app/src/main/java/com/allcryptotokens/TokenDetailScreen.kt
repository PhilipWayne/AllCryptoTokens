package com.allcryptotokens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenDetailScreen(
    cgId: String
) {
    val ctx = LocalContext.current
    val dao = remember { AppDatabase.get(ctx).tokenDao() }

    val token by dao.observeToken(cgId).collectAsState(initial = null)
    val scroll = rememberScrollState()

    val title = token?.let { "${it.name} (${it.symbol.uppercase()})" } ?: cgId
    val desc = token?.description?.takeIf { it.isNotBlank() } ?: "No description yet."

    Scaffold(
        topBar = { TopAppBar(title = { Text(title) }) }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .verticalScroll(scroll)
        ) {
            AsyncImage(
                model = token?.imageUrl?.takeIf { it.isNotBlank() },
                contentDescription = null,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(desc)
        }
    }
}
