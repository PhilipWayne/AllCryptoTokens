package com.allcryptotokens

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import retrofit2.HttpException

/**
 * Background refresher for cached token details.
 * Gentle on rate limits; skips errors and continues.
 */
class TokenSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val ctx = applicationContext
        val dao = AppDatabase.get(ctx).tokenDao()
        val api = NetworkModule.coinGeckoService(debug = false)

        return try {
            val ids = dao.listAllIds()
            for (id in ids) {
                try {
                    val d = api.coinDetail(id)
                    val desc = d.description?.en?.let { stripHtml(it) }?.ifBlank { null }
                    val img = d.image?.large ?: d.image?.small ?: d.image?.thumb
                    dao.updateDetails(id, desc, img, System.currentTimeMillis())
                    delay(1200L) // ~1.2s between requests
                } catch (e: HttpException) {
                    if (e.code() == 429) delay(4000L) // brief backoff, continue
                } catch (_: Exception) {
                    // ignore and continue
                }
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
