// CoinDetailResponse.kt
package com.allcryptotokens

data class CoinDetailResponse(
    val id: String?,
    val symbol: String?,
    val name: String?,
    val description: CoinDescription?,
    val image: CoinImage?
)

data class CoinDescription(
    val en: String? // we only need English
)

data class CoinImage(
    val thumb: String?,
    val small: String?,
    val large: String?
)
