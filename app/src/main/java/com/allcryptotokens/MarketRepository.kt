package com.allcryptotokens

class MarketRepository(
    private val service: MarketApiService
) {
    suspend fun getCurrent(cgId: String): MarketCurrentData? {
        val response = service.getMarketCurrent(cgId)
        return if (response.success) response.data else null
    }


    suspend fun getPriceHistory(cgId: String, range: String): List<PriceHistoryPoint> {
        val response = service.getPriceHistory(cgId, range)
        return if (response.success) response.data?.points.orEmpty() else emptyList()
    }
}
