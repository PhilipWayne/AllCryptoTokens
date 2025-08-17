// app/src/main/java/com/allcryptotokens/HtmlUtils.kt
package com.allcryptotokens

import androidx.core.text.HtmlCompat

/** Clean CoinGecko HTML to plain text, decoding entities. */
fun stripHtml(src: String): String =
    HtmlCompat.fromHtml(src, HtmlCompat.FROM_HTML_MODE_LEGACY)
        .toString()
        .replace("\u00A0", " ") // decode non-breaking spaces
        .trim()
