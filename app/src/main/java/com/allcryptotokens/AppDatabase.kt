package com.allcryptotokens

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TokenEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tokenDao(): TokenDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create new table with strict schema
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tokens_new(
                      cgId        TEXT NOT NULL PRIMARY KEY,
                      symbol      TEXT NOT NULL,
                      name        TEXT NOT NULL,
                      description TEXT,
                      imageUrl    TEXT,
                      updatedAt   INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                // Copy data, replace NULLs with safe defaults
                db.execSQL(
                    """
                    INSERT INTO tokens_new (cgId, symbol, name, description, imageUrl, updatedAt)
                    SELECT
                      COALESCE(cgId, ''),
                      COALESCE(symbol, ''),
                      COALESCE(name, ''),
                      description,
                      imageUrl,
                      COALESCE(updatedAt, 0)
                    FROM tokens
                    """.trimIndent()
                )

                // Drop old table and rename
                db.execSQL("DROP TABLE tokens")
                db.execSQL("ALTER TABLE tokens_new RENAME TO tokens")

                // Recreate indices
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_tokens_symbol ON tokens(symbol)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_tokens_name ON tokens(name)")
            }
        }

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "allcryptotokens.db"
                )
                    .createFromAsset("prebuilt_tokens.db")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
