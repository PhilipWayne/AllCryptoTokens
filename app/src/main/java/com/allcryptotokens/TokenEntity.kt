package com.allcryptotokens

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tokens")
data class TokenEntity(
    @PrimaryKey val cgId: String,     // CoinGecko id
    val name: String,
    val symbol: String,
    val description: String?,         // plain text (HTML stripped)
    val imageUrl: String?,            // keep remote url; Coil caches bytes
    val updatedAt: Long,              // last time we refreshed from network
    val sourceVersion: Int = 1        // bump if your parsing rules change
)
