package com.allcryptotokens

import android.content.Context
import kotlinx.coroutines.flow.Flow

class TokenRepository(
    context: Context
) {
    private val dao = AppDatabase.get(context).tokenDao()

    fun observeAllTokens(): Flow<List<TokenEntity>> = dao.observeAllTokens()
    fun observeToken(cgId: String): Flow<TokenEntity?> = dao.observeToken(cgId)
    fun searchTokens(q: String): Flow<List<TokenEntity>> = dao.searchTokens(q)
}
