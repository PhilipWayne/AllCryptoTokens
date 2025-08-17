// app/src/main/java/com/allcryptotokens/NetworkModule.kt
package com.allcryptotokens

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object NetworkModule {

    private fun httpClient(context: Context?, debug: Boolean): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (debug) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.BASIC
        }
        val b = OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("User-Agent", "AllCryptoTokens/1.0")
                        .build()
                )
            }
            .addInterceptor(logging)
            .readTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)

        if (context != null) {
            val cacheDir = File(context.cacheDir, "http_cache")
            b.cache(Cache(cacheDir, 10L * 1024L * 1024L))
            b.addNetworkInterceptor { chain ->
                val req = chain.request()
                val res = chain.proceed(req)
                if (req.method == "GET" && res.header("Cache-Control") == null) {
                    res.newBuilder().header("Cache-Control", "public, max-age=300").build()
                } else res
            }
        }
        return b.build()
    }

    fun coinGeckoService(context: Context, debug: Boolean = false): CoinGeckoApiService =
        Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .client(httpClient(context, debug))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinGeckoApiService::class.java)

    fun coinGeckoService(debug: Boolean = false): CoinGeckoApiService =
        Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .client(httpClient(null, debug))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinGeckoApiService::class.java)

    fun cryptoComService(debug: Boolean = false): CryptoComApiService =
        Retrofit.Builder()
            .baseUrl("https://api.crypto.com/v2/")
            .client(httpClient(null, debug))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CryptoComApiService::class.java)
}
