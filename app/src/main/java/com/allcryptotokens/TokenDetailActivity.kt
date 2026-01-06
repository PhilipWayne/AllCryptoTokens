package com.allcryptotokens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.allcryptotokens.ui.theme.AllCryptoTokensTheme

class TokenDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cgId = intent.getStringExtra("cgId") ?: return

        setContent {
            AllCryptoTokensTheme {
                TokenDetailScreen(cgId = cgId)
            }
        }
    }
}
