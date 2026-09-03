package com.allcryptotokens

import com.google.gson.annotations.SerializedName

data class PriceHistoryResponse(
    val success: Boolean,
    val data: PriceHistoryData?
)

data class PriceHistoryData(
    @SerializedName("cg_id")
    val cgId: String,
    val range: String,
    val points: List<PriceHistoryPoint>
)

data class PriceHistoryPoint(
    val price: Double,
    val timestamp: Long
)
