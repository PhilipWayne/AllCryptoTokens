package com.allcryptotokens

class MarketRepository(
    private val service: MarketApiService
) {
    suspend fun getCurrent(cgId: String): MarketCurrentData? {
        val response = service.getMarketCurrent(cgId)
        return if (response.success) response.data else null
    }
}
