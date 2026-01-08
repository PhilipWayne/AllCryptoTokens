package com.allcryptotokens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.allcryptotokens.ui.theme.AllCryptoTokensTheme

class AboutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AllCryptoTokensTheme {
                AboutScreen(
                    privacyUrl = "https://philipwayne.github.io/AllCryptoTokens/privacy-policy.html",
                    onOpenUrl = { url ->
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutScreen(
    privacyUrl: String,
    onOpenUrl: (String) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("About") }) }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("All Crypto Tokens", style = MaterialTheme.typography.headlineSmall)
            Text("Informational app. Not financial advice.", style = MaterialTheme.typography.bodyMedium)

            Button(onClick = { onOpenUrl(privacyUrl) }) {
                Text("Privacy Policy")
            }
        }
    }
}
