package com.allcryptotokens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.allcryptotokens.ui.theme.AllCryptoTokensTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AllCryptoTokensTheme {
                TokenListScreen(
                    onOpenToken = { cgId ->
                        startActivity(
                            Intent(this, TokenDetailActivity::class.java).apply {
                                putExtra("cgId", cgId)
                            }
                        )
                    },
                    onOpenAbout = {
                        startActivity(Intent(this, AboutActivity::class.java))
                    }
                )
            }
        }
    }
}
