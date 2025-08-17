// app/src/main/java/com/allcryptotokens/OfflineFirstTokenRepository.kt
package com.allcryptotokens

import android.content.Context
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class OfflineFirstTokenRepository(
    context: Context,
    private val api: CoinGeckoApiService
) {
    private val dao = AppDatabase.get(context).tokenDao()

    // Refresh descriptions at most every 30 days
    private val ttlMs = 30L * 24 * 60 * 60 * 1000

    fun observe(cgId: String): Flow<TokenEntity?> = dao.observeToken(cgId)

    suspend fun ensureCached(
        cgId: String,
        name: String,
        symbol: String,
        imageUrl: String?
    ): TokenEntity {
        val now = System.currentTimeMillis()
        val existing = dao.getTokenOnce(cgId)

        val needsFetch = existing == null ||
                existing.description.isNullOrBlank() ||
                now - existing.updatedAt > ttlMs
        if (!needsFetch) return existing!!

        return try {
            val d = api.coinDetail(cgId)

            val desc = d.description?.en
                ?.let { stripHtml(it) }
                ?.trim()
                ?.takeIf { it.isNotEmpty() }

            val fetchedImg = d.image?.large ?: d.image?.small ?: d.image?.thumb
            val bestImg = fetchedImg ?: imageUrl ?: existing?.imageUrl

            val next = TokenEntity(
                cgId = cgId,
                name = name,
                symbol = symbol,
                description = desc ?: existing?.description, // don't downgrade
                imageUrl = bestImg,
                updatedAt = now
            )

            if (next != existing) dao.upsert(next)
            next
        } catch (e: Exception) {
            when (e) {
                is HttpException -> { /* ignore, fall back */ }
                else -> { /* ignore, fall back */ }
            }
            val fallback = existing ?: TokenEntity(
                cgId = cgId,
                name = name,
                symbol = symbol,
                description = null,
                imageUrl = imageUrl,
                updatedAt = now
            )
            if (existing == null) dao.upsert(fallback)
            fallback
        }
    }
}
