package com.allcryptotokens

import com.google.gson.annotations.SerializedName

data class MarketCurrentResponse(
    val success: Boolean,
    val data: MarketCurrentData?
)

data class MarketCurrentData(
    @SerializedName("cg_id")
    val cgId: String,
    val price: Double?,
    @SerializedName("change_24h")
    val change24h: Double?,
    @SerializedName("market_cap")
    val marketCap: Double?,
    @SerializedName("volume_24h")
    val volume24h: Double?,
    val rank: Int?,
    @SerializedName("all_time_high_price")
    val allTimeHighPrice: Double?,
    @SerializedName("all_time_high_timestamp")
    val allTimeHighTimestamp: Long?,
    @SerializedName("updated_at")
    val updatedAt: Long
)
