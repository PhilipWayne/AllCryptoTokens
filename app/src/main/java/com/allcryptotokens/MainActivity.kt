// app/src/main/java/com/allcryptotokens/MainActivity.kt
package com.allcryptotokens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // Offline list — reads from prebuilt Room DB
                TokenListScreen()
            }
        }
    }
}
