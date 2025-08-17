package com.allcryptotokens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory   // <-- use Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cryptoComApi: CryptoComApiService = Retrofit.Builder()
            .baseUrl("https://api.crypto.com/v2/")
            .addConverterFactory(GsonConverterFactory.create())   // <-- use Gson
            .build()
            .create(CryptoComApiService::class.java)

        setContent {
            MaterialTheme {
                TokenListScreen(api = cryptoComApi)
            }
        }
    }
}
