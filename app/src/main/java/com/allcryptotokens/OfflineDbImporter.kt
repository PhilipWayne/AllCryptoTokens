package com.allcryptotokens

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Variant 2 (correct): On app update, import tokens from assets prebuilt DB into the local Room DB.
 *
 * Key idea:
 * - Use PRAGMA user_version inside assets/prebuilt_tokens.db as the "data version".
 * - Store last imported data version in SharedPreferences.
 * - If assets version > imported version -> upsert (INSERT OR REPLACE) into Room.
 *
 * This updates tokens/descriptions automatically for existing users WITHOUT reinstall / clear data.
 */
object OfflineDbImporter {

    private const val ASSET_DB = "prebuilt_tokens.db"
    private const val PREFS = "offline_db_importer"
    private const val KEY_IMPORTED_USER_VERSION = "imported_user_version"

    suspend fun syncIfNeeded(context: Context, dao: TokenDao) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val importedVersion = prefs.getInt(KEY_IMPORTED_USER_VERSION, 0)

        // Copy asset DB into a temp file (assets cannot be opened as sqlite directly in all cases)
        val tmp = File.createTempFile("asset_prebuilt_", ".db", context.cacheDir)
        context.assets.open(ASSET_DB).use { input ->
            FileOutputStream(tmp).use { out -> input.copyTo(out) }
        }

        val src = SQLiteDatabase.openDatabase(tmp.absolutePath, null, SQLiteDatabase.OPEN_READONLY)
        val assetUserVersion = src.rawQuery("PRAGMA user_version", null).use { c ->
            if (c.moveToFirst()) c.getInt(0) else 0
        }

        // If no change -> nothing to do
        if (assetUserVersion <= importedVersion) {
            src.close()
            tmp.delete()
            return@withContext
        }

        // Import tokens (upsert by primary key cgId)
        val cursor = src.rawQuery(
            "SELECT cgId, symbol, name, description, imageUrl, updatedAt FROM tokens",
            null
        )

        val iCgId = cursor.getColumnIndexOrThrow("cgId")
        val iSymbol = cursor.getColumnIndexOrThrow("symbol")
        val iName = cursor.getColumnIndexOrThrow("name")
        val iDesc = cursor.getColumnIndexOrThrow("description")
        val iImg = cursor.getColumnIndexOrThrow("imageUrl")
        val iUpd = cursor.getColumnIndexOrThrow("updatedAt")

        val batch = ArrayList<TokenEntity>(500)
        while (cursor.moveToNext()) {
            val cgId = cursor.getString(iCgId)
            val symbol = cursor.getString(iSymbol)
            val name = cursor.getString(iName)

            val desc = if (cursor.isNull(iDesc)) null else cursor.getString(iDesc)
            val img = if (cursor.isNull(iImg)) null else cursor.getString(iImg)
            val updatedAt = if (cursor.isNull(iUpd)) 0L else cursor.getLong(iUpd)

            batch.add(
                TokenEntity(
                    cgId = cgId,
                    symbol = symbol,
                    name = name,
                    description = desc,
                    imageUrl = img,
                    updatedAt = updatedAt
                )
            )

            if (batch.size >= 500) {
                dao.insertAll(batch) // INSERT OR REPLACE
                batch.clear()
            }
        }

        if (batch.isNotEmpty()) dao.insertAll(batch)

        cursor.close()
        src.close()
        tmp.delete()

        // Mark imported version
        prefs.edit().putInt(KEY_IMPORTED_USER_VERSION, assetUserVersion).apply()
    }
}
