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

    // ✅ Search ONLY by user-friendly fields (name + symbol)
    @Query(
        """
        SELECT * FROM tokens
        WHERE (name LIKE '%' || :q || '%' COLLATE NOCASE)
           OR (symbol LIKE '%' || :q || '%' COLLATE NOCASE)
        ORDER BY name COLLATE NOCASE ASC
        """
    )
    fun searchTokens(q: String): Flow<List<TokenEntity>>

    @Query("SELECT COUNT(*) FROM tokens")
    suspend fun countTokens(): Int

    // Used by OfflineDbImporter
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tokens: List<TokenEntity>)
}
