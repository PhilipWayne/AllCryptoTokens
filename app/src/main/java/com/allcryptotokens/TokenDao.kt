package com.allcryptotokens

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {

    @Query("SELECT * FROM tokens ORDER BY name COLLATE NOCASE ASC")
    fun observeAllTokens(): Flow<List<TokenEntity>>

    @Query("SELECT * FROM tokens WHERE cgId = :cgId LIMIT 1")
    fun observeToken(cgId: String): Flow<TokenEntity?>

    @Query("SELECT * FROM tokens WHERE cgId = :cgId LIMIT 1")
    suspend fun getTokenOnce(cgId: String): TokenEntity?

    @Query(
        """
        SELECT * FROM tokens
        WHERE (name LIKE '%' || :q || '%' COLLATE NOCASE)
           OR (symbol LIKE '%' || :q || '%' COLLATE NOCASE)
           OR (cgId LIKE '%' || :q || '%' COLLATE NOCASE)
        ORDER BY name COLLATE NOCASE ASC
        """
    )
    fun searchTokens(q: String): Flow<List<TokenEntity>>

    @Query("SELECT COUNT(*) FROM tokens")
    suspend fun countTokens(): Int

    // Not used in normal flow now, but harmless to keep
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tokens: List<TokenEntity>)
}
