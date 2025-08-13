package com.allcryptotokens

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private fun httpClient(debug: Boolean): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (debug) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    // CoinGecko (for names/descriptions/logos)
    fun coinGeckoService(debug: Boolean = true): CoinGeckoApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/")
            .client(httpClient(debug))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinGeckoApiService::class.java)
    }

    // (Optional) Crypto.com you already had:
    fun cryptoComService(debug: Boolean = true): CryptoComApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.crypto.com/")
            .client(httpClient(debug))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CryptoComApiService::class.java)
    }
}
