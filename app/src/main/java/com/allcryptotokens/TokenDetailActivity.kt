// app/src/main/java/com/allcryptotokens/TokenDetailActivity.kt
package com.allcryptotokens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class TokenDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rawId = intent.getStringExtra("cg_id")
        if (rawId.isNullOrBlank()) {
            Log.w("TokenDetailActivity", "Missing cg_id extra; finishing Activity")
            finish()
            return
        }
        val id = rawId

        val symbol = intent.getStringExtra("symbol") ?: ""
        val name = intent.getStringExtra("name") ?: symbol.ifBlank { id }
        val imageUrl = intent.getStringExtra("imageUrl")

        setContent {
            MaterialTheme {
                TokenDetailScreen(
                    cgId = id,
                    name = name,
                    symbol = symbol,
                    initialImageUrl = imageUrl
                )
            }
        }
    }
}
