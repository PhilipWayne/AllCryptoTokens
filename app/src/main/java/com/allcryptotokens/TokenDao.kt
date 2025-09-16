package com.allcryptotokens

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {

    // Live list of all tokens (alphabetical, case-insensitive)
    @Query("SELECT * FROM tokens ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<TokenEntity>>

    @Query("SELECT * FROM tokens WHERE cgId = :cgId LIMIT 1")
    fun observeToken(cgId: String): Flow<TokenEntity?>

    @Query("SELECT * FROM tokens WHERE cgId = :cgId LIMIT 1")
    suspend fun getTokenOnce(cgId: String): TokenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(token: TokenEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(tokens: List<TokenEntity>)

    @Query("UPDATE tokens SET description = :desc, imageUrl = :img, updatedAt = :ts WHERE cgId = :cgId")
    suspend fun updateDetails(cgId: String, desc: String?, img: String?, ts: Long)

    @Query("SELECT cgId FROM tokens")
    suspend fun listAllIds(): List<String>

    @Query("SELECT * FROM tokens")
    suspend fun getAllTokens(): List<TokenEntity>

    @Query("DELETE FROM tokens")
    suspend fun clearAll()
}
