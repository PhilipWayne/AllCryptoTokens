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
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tokenDao(): TokenDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DB_NAME = "allcryptotokens.db"
        private const val ASSET_DB = "prebuilt_tokens.db"

        /**
         * Returns a singleton Room database instance.
         *
         * IMPORTANT:
         * We always call ensureLatestAssetDbApplied() BEFORE Room is created.
         * This allows us to force-update the installed DB if the assets DB version is newer.
         *
         * This is the correct way to make existing users receive new offline token descriptions
         * automatically after app update.
         */
        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val appCtx = context.applicationContext

                // Critical: update installed DB from assets if needed BEFORE opening Room.
                ensureLatestAssetDbApplied(appCtx)

                val db = Room.databaseBuilder(
                    appCtx,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // Pre-packaged DB stored in app/src/main/assets/prebuilt_tokens.db
                    .createFromAsset(ASSET_DB)

                    /**
                     * NOTE:
                     * We do NOT enable fallbackToDestructiveMigration() by default.
                     * If Room schema version changes, you should either provide migrations
                     * or bump user_version in assets and allow asset DB replacement.
                     */

                    // If you ever downgrade Room version (you should avoid it), you can enable this:
                    // .fallbackToDestructiveMigrationOnDowngrade()
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()

                INSTANCE = db
                db
            }
        }

        /**
         * Ensures that the newest prebuilt_tokens.db from assets is applied for existing users.
         *
         * Strategy:
         * - Read PRAGMA user_version from installed DB file (if exists).
         * - Read PRAGMA user_version from assets DB.
         * - If assets DB is newer, delete installed DB so Room will recreate it from assets.
         *
         * This avoids manual reinstall and guarantees that offline descriptions update automatically.
         */
        private fun ensureLatestAssetDbApplied(context: Context) {
            try {
                val installedDbFile = context.getDatabasePath(DB_NAME)

                val installedVersion = if (installedDbFile.exists()) {
                    readUserVersionFromFile(installedDbFile)
                } else {
                    0
                }

                val assetVersion = readUserVersionFromAsset(context, ASSET_DB)

                // If assets DB has higher user_version, replace installed DB.
                if (assetVersion > installedVersion) {
                    context.deleteDatabase(DB_NAME)
                }
            } catch (_: Throwable) {
                // Never crash the app on startup due to DB update logic.
                // If something goes wrong, we keep using the installed DB.
            }
        }

        /**
         * Reads PRAGMA user_version from an SQLite DB file.
         */
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

        /**
         * Reads PRAGMA user_version from an assets DB.
         *
         * Assets DB cannot be opened directly as a file path,
         * so we copy it into cacheDir temporarily.
         */
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
