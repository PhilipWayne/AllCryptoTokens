package com.allcryptotokens

import android.content.Context
import kotlinx.coroutines.flow.Flow

/**
 * Offline-only repository.
 * Uses the prebuilt DB copied from assets.
 */
class OfflineFirstTokenRepository(
    context: Context
) {
    private val dao = AppDatabase.get(context.applicationContext).tokenDao()


    fun observe(cgId: String): Flow<TokenEntity?> = dao.observeToken(cgId)
    fun observeAll(): Flow<List<TokenEntity>> = dao.observeAllTokens()
    fun search(q: String): Flow<List<TokenEntity>> = dao.searchTokens(q)
}
