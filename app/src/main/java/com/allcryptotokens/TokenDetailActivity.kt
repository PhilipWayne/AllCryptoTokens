package com.allcryptotokens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import com.allcryptotokens.ui.ads.AdSupportedContent
import com.allcryptotokens.ui.theme.AllCryptoTokensTheme

class TokenDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val cgId = intent.getStringExtra("cgId") ?: return

        setContent {
            AllCryptoTokensTheme {
                AdSupportedContent {
                    TokenDetailScreen(cgId = cgId)
                }
            }
        }
    }
}
