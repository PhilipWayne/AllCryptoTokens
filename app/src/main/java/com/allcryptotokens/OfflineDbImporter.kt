package com.allcryptotokens

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object OfflineDbImporter {

    private const val ASSET_DB = "prebuilt_tokens.db"
    private const val PREFS = "offline_db_importer"
    private const val KEY_IMPORTED_USER_VERSION = "imported_user_version"

    // syncs asset DB into Room if asset version is newer
    suspend fun syncIfNeeded(context: Context, dao: TokenDao) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val importedVersion = prefs.getInt(KEY_IMPORTED_USER_VERSION, 0)

        val tmp = File.createTempFile("asset_prebuilt_", ".db", context.cacheDir)
        context.assets.open(ASSET_DB).use { input ->
            FileOutputStream(tmp).use { out -> input.copyTo(out) }
        }

        val src = SQLiteDatabase.openDatabase(tmp.absolutePath, null, SQLiteDatabase.OPEN_READONLY)

        try {
            val assetUserVersion = src.rawQuery("PRAGMA user_version", null).use { c ->
                if (c.moveToFirst()) c.getInt(0) else 0
            }

            if (assetUserVersion <= importedVersion) {
                return@withContext
            }

            val cursor = src.rawQuery(
                "SELECT cgId, symbol, name, description, imageUrl, updatedAt, youtubeId FROM tokens",
                null
            )

            val iCgId = cursor.getColumnIndexOrThrow("cgId")
            val iSymbol = cursor.getColumnIndexOrThrow("symbol")
            val iName = cursor.getColumnIndexOrThrow("name")
            val iDesc = cursor.getColumnIndexOrThrow("description")
            val iImg = cursor.getColumnIndexOrThrow("imageUrl")
            val iUpd = cursor.getColumnIndexOrThrow("updatedAt")
            val iYoutube = cursor.getColumnIndexOrThrow("youtubeId")

            val batch = ArrayList<TokenEntity>(500)

            while (cursor.moveToNext()) {
                val cgId = cursor.getString(iCgId)
                val symbol = cursor.getString(iSymbol)
                val name = cursor.getString(iName)
                val desc = if (cursor.isNull(iDesc)) null else cursor.getString(iDesc)
                val img = if (cursor.isNull(iImg)) null else cursor.getString(iImg)
                val updatedAt = if (cursor.isNull(iUpd)) 0L else cursor.getLong(iUpd)
                val youtubeId = if (cursor.isNull(iYoutube)) null else cursor.getString(iYoutube)

                batch.add(
                    TokenEntity(
                        cgId = cgId,
                        symbol = symbol,
                        name = name,
                        description = desc,
                        imageUrl = img,
                        updatedAt = updatedAt,
                        youtubeId = youtubeId
                    )
                )

                if (batch.size >= 500) {
                    dao.insertAll(batch)
                    batch.clear()
                }
            }

            cursor.close()

            if (batch.isNotEmpty()) {
                dao.insertAll(batch)
            }

            prefs.edit().putInt(KEY_IMPORTED_USER_VERSION, assetUserVersion).apply()
        } finally {
            src.close()
            tmp.delete()
        }
    }
}