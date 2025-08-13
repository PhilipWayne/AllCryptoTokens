package com.allcryptotokens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class TokenDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cgId = intent.getStringExtra("cg_id") ?: ""
        val symbol = intent.getStringExtra("symbol") ?: ""
        val name = intent.getStringExtra("name") ?: symbol
        val imageUrl = intent.getStringExtra("imageUrl")

        setContent {
            MaterialTheme {
                TokenDetailScreen(
                    cgId = cgId,
                    name = name,
                    symbol = symbol,
                    initialImageUrl = imageUrl
                )
            }
        }
    }
}
