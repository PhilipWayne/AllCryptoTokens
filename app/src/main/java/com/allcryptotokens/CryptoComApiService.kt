package com.allcryptotokens

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

interface CryptoComApiService {
    @GET("/exchange/v1/public/get-instruments")
    suspend fun getInstruments(): Envelope<InstrumentsResult>

    @GET("/exchange/v1/public/get-tickers")
    suspend fun getTickers(): Envelope<TickersResult>
}

/** Generic response envelope */
data class Envelope<T>(
    val id: Long? = null,
    val method: String? = null,
    val code: Int = -1,
    val result: T? = null
)

/** Instruments can appear under result.data OR result.instruments */
data class InstrumentsResult(
    @SerializedName("data") val data: List<Instrument> = emptyList(),
    @SerializedName("instruments") val instruments: List<Instrument> = emptyList()
)

/** Accept common key aliases from Crypto.com */
data class Instrument(
    // instrument/symbol name, e.g. "BTC_USDT"
    @SerializedName(value = "instrument_name", alternate = ["symbol", "name"])
    val instrument_name: String? = null,

    // base currency, e.g. "BTC"
    @SerializedName(value = "base_currency", alternate = ["base_ccy", "base"])
    val base_currency: String? = null,

    // quote currency, e.g. "USDT"
    @SerializedName(value = "quote_currency", alternate = ["quote_ccy", "quote"])
    val quote_currency: String? = null,

    // type/category, e.g. "CCY_PAIR", "SPOT", "PERPETUAL_SWAP", "FUTURE"
    @SerializedName(value = "instrument_type", alternate = ["type", "inst_type", "category"])
    val instrument_type: String? = null
)

/** Tickers always show up in result.data */
data class TickersResult(
    @SerializedName("data") val data: List<Ticker> = emptyList()
)

data class Ticker(
    // instrument name this ticker corresponds to
    val i: String,
    // last price
    val a: String? = null,
    // 24h change (percent)
    val c: String? = null,
    val t: Long? = null
)
