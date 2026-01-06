package com.allcryptotokens

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object OfflineDbImporter {

    private const val ASSET_DB = "prebuilt_tokens.db"
    private const val MARKER = "offline_import_done.marker"

    suspend fun importIfNeeded(context: Context, dao: TokenDao) = withContext(Dispatchers.IO) {
        // already imported?
        if (dao.countTokens() > 0) return@withContext

        val marker = File(context.filesDir, MARKER)
        if (marker.exists()) return@withContext

        // copy asset db into cache temp file
        val tmp = File(context.cacheDir, "tmp_prebuilt_tokens.db")
        context.assets.open(ASSET_DB).use { input ->
            FileOutputStream(tmp).use { output -> input.copyTo(output) }
        }

        val src = SQLiteDatabase.openDatabase(
            tmp.absolutePath,
            null,
            SQLiteDatabase.OPEN_READONLY
        )

        val cursor = src.rawQuery(
            """
SELECT
  cgId,
  symbol,
  name,
  description,
  imageUrl,
  updatedAt
FROM tokens
""".trimIndent(),
            null
        )

        val batch = ArrayList<TokenEntity>(500)

        while (cursor.moveToNext()) {
            batch.add(
                TokenEntity(
                    cgId = cursor.getString(0),
                    symbol = cursor.getString(1),
                    name = cursor.getString(2),
                    description = cursor.getStringOrNull(3),
                    imageUrl = cursor.getStringOrNull(4),
                    updatedAt = cursor.getLong(5)
                )
            )

            if (batch.size >= 500) {
                dao.insertAll(batch)
                batch.clear()
            }
        }

        if (batch.isNotEmpty()) dao.insertAll(batch)

        cursor.close()
        src.close()
        tmp.delete()

        marker.writeText("ok")
    }

    private fun android.database.Cursor.getStringOrNull(i: Int): String? =
        if (isNull(i)) null else getString(i)
}
