package com.allcryptotokens

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoApiService {
    // 1) Full coin catalog (id, symbol, name)
    @GET("/api/v3/coins/list")
    suspend fun coinsList(
        @Query("include_platform") includePlatform: Boolean = false
    ): List<CgCoinListItem>

    // 2) Optional: markets (not required for your current screen)
    @GET("/api/v3/coins/markets")
    suspend fun markets(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 250,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false,
        @Query("ids") ids: String? = null
    ): List<CgMarketItem>

    // 3) Detail for one coin (for description + image)
    @GET("/api/v3/coins/{id}")
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

data class CgDescription(val en: String? = null)
data class CgImage(val thumb: String? = null, val small: String? = null, val large: String? = null)
