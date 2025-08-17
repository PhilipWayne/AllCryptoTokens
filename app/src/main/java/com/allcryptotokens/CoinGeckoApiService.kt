// app/src/main/java/com/allcryptotokens/CoinGeckoApiService.kt
package com.allcryptotokens

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName

interface CoinGeckoApiService {

    @GET("search")
    suspend fun search(@Query("query") query: String): CgSearchResponse

    @GET("coins/list")
    suspend fun coinsList(
        @Query("include_platform") includePlatform: Boolean = false
    ): List<CgCoinListItem>

    @GET("coins/markets")
    suspend fun markets(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 250,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false,
        @Query("ids") ids: String? = null
    ): List<CgMarketItem>

    @GET("coins/{id}")
    suspend fun coinDetail(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = false,
        @Query("tickers") tickers: Boolean = false,
        @Query("market_data") marketData: Boolean = false,
        @Query("community_data") community: Boolean = false,
        @Query("developer_data") developer: Boolean = false,
        @Query("sparkline") sparkline: Boolean = false
    ): CgCoinDetail
}

// ===== Models =====

data class CgCoinListItem(
    val id: String,
    val symbol: String,
    val name: String
)

data class CgMarketItem(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String? = null
)

data class CgCoinDetail(
    val id: String,
    val symbol: String,
    val name: String,
    val description: CgDescription? = null,
    val image: CgImage? = null
)

data class CgSearchResponse(val coins: List<CgSearchCoin> = emptyList())
data class CgSearchCoin(
    val id: String,
    val name: String,
    val symbol: String,
    @SerializedName("market_cap_rank") val marketCapRank: Int? = null
)

data class CgDescription(val en: String? = null)
data class CgImage(val thumb: String? = null, val small: String? = null, val large: String? = null)
