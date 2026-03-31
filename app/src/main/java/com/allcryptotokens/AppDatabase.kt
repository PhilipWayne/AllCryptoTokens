package com.allcryptotokens

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File
import java.io.FileOutputStream

@Database(
    entities = [TokenEntity::class],
    version = 20,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tokenDao(): TokenDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DB_NAME = "allcryptotokens.db"
        private const val ASSET_DB = "prebuilt_tokens.db"

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val appCtx = context.applicationContext

                ensureLatestAssetDbApplied(appCtx)

                val db = Room.databaseBuilder(
                    appCtx,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .createFromAsset(ASSET_DB)
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()

                INSTANCE = db
                db
            }
        }

        // ensures app DB is replaced if asset DB has newer user_version
        private fun ensureLatestAssetDbApplied(context: Context) {
            try {
                val installedDbFile = context.getDatabasePath(DB_NAME)

                val installedVersion = if (installedDbFile.exists()) {
                    readUserVersionFromFile(installedDbFile)
                } else {
                    0
                }

                val assetVersion = readUserVersionFromAsset(context, ASSET_DB)

                if (assetVersion > installedVersion) {
                    context.deleteDatabase(DB_NAME)
                }
            } catch (_: Throwable) {
                // do not crash on DB check failure
            }
        }

        private fun readUserVersionFromFile(dbFile: File): Int {
            val db = SQLiteDatabase.openDatabase(
                dbFile.absolutePath,
                null,
                SQLiteDatabase.OPEN_READONLY
            )

            return try {
                db.rawQuery("PRAGMA user_version", null).use { cursor ->
                    if (cursor.moveToFirst()) cursor.getInt(0) else 0
                }
            } finally {
                db.close()
            }
        }

        private fun readUserVersionFromAsset(context: Context, assetName: String): Int {
            val tmpFile = File.createTempFile("asset_prebuilt_", ".db", context.cacheDir)

            try {
                context.assets.open(assetName).use { input ->
                    FileOutputStream(tmpFile).use { output ->
                        input.copyTo(output)
                    }
                }

                return readUserVersionFromFile(tmpFile)
            } finally {
                tmpFile.delete()
            }
        }
    }
}