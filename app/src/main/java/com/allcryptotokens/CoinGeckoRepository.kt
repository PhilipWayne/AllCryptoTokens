package com.allcryptotokens

import kotlinx.coroutines.delay
import retrofit2.HttpException

// Replace with your actual response model type returned by coinDetail()
typealias CoinDetail = CgCoinDetail

class CoinGeckoRepository(
    private val service: CoinGeckoApiService
) {
    private data class Entry<T>(val value: T, val ts: Long)

    // Simple in-memory cache with TTL
    private val cache = mutableMapOf<String, Entry<CoinDetail>>()

    // Tune as you like (in ms). 10 minutes is a good start.
    private val ttlMs = 10 * 60 * 1000L

    suspend fun getDetail(cgId: String): CoinDetail {
        val now = System.currentTimeMillis()
        cache[cgId]?.let { e ->
            if (now - e.ts < ttlMs) return e.value
        }

        // Fetch with gentle retry if rate-limited
        var backoff = 1000L
        while (true) {
            try {
                val d = service.coinDetail(cgId)
                cache[cgId] = Entry(d, System.currentTimeMillis())
                return d
            } catch (e: HttpException) {
                if (e.code() == 429) {
                    val retryAfter = e.response()?.headers()?.get("Retry-After")?.toLongOrNull()
                    delay((retryAfter?.times(1000)) ?: backoff)
                    backoff = (backoff * 2).coerceAtMost(12_000L)
                    continue
                }
                throw e
            }
        }
    }
}
