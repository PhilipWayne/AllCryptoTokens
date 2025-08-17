package com.allcryptotokens.com.allcryptotokens

import android.content.Context
import com.allcryptotokens.AppDatabase
import com.allcryptotokens.TokenEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

object Seed {
    private var done = false

    suspend fun run(context: Context) = withContext(Dispatchers.IO) {
        if (done) return@withContext
        val dao = AppDatabase.get(context).tokenDao()
        try {
            val stream = context.assets.open("tokens_seed.json")
            val text = stream.reader().readText()
            val arr = JSONArray(text)
            val now = System.currentTimeMillis()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)

                val desc = o.optString("description", "").takeIf { it.isNotBlank() }
                val img  = o.optString("imageUrl", "").takeIf { it.isNotBlank() }

                val entity = TokenEntity(
                    cgId = o.getString("cgId"),
                    name = o.getString("name"),
                    symbol = o.getString("symbol"),
                    description = desc,
                    imageUrl = img,
                    updatedAt = if (desc != null) now else 0L
                )
                dao.upsert(entity)
            }
        } catch (_: Exception) {
            // no seed present or parse issue; ignore
        }
        done = true
    }
}
