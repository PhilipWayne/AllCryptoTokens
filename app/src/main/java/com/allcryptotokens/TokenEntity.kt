package com.allcryptotokens

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tokens",
    indices = [
        Index(value = ["name"], name = "idx_tokens_name"),
        Index(value = ["symbol"], name = "idx_tokens_symbol")
    ]
)
data class TokenEntity(
    @PrimaryKey val cgId: String,
    val symbol: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val updatedAt: Long
)
