package com.allcryptotokens

import retrofit2.http.GET
import retrofit2.http.Query

interface MarketApiService {
    @GET("market-current")
    suspend fun getMarketCurrent(
        @Query("cgId") cgId: String
    ): MarketCurrentResponse
}
