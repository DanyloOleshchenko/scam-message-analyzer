package com.example.scammessageanalyzer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [IncidentEntity::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun incidentDao(): IncidentDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val migrationFrom1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE incident_reports ADD COLUMN sourceType TEXT NOT NULL DEFAULT 'manual'"
                )
                db.execSQL("ALTER TABLE incident_reports ADD COLUMN sender TEXT")
                db.execSQL("ALTER TABLE incident_reports ADD COLUMN sourceMessageId TEXT")
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_incident_reports_sourceType_sourceMessageId " +
                        "ON incident_reports(sourceType, sourceMessageId)"
                )
            }
        }

        private val migrationFrom2To3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE incident_reports ADD COLUMN isDismissed INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        private val migrationFrom3To6 = object : Migration(3, 6) {
            override fun migrate(db: SupportSQLiteDatabase) = Unit
        }

        private val migrationFrom4To6 = object : Migration(4, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    UPDATE incident_reports
                    SET riskScore = ROUND((MIN(riskScore, 100) * 129.0) / 100.0)
                    """
                )
            }
        }

        private val migrationFrom5To6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    UPDATE incident_reports
                    SET riskScore = CASE
                        WHEN riskScore <= 0 THEN 0
                        WHEN riskScore < 40
                            THEN ROUND(riskScore * 19.0 / 39.0)
                        WHEN riskScore < 80
                            THEN 20 + ROUND((riskScore - 40) * 24.0 / 39.0)
                        ELSE 45 + ROUND((MIN(riskScore, 100) - 80) * 84.0 / 20.0)
                    END
                    """
                )
            }
        }

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "scam_analyzer.db"
                )
                    .addMigrations(
                        migrationFrom1To2,
                        migrationFrom2To3,
                        migrationFrom3To6,
                        migrationFrom4To6,
                        migrationFrom5To6
                    )
                    .build()
                    .also { instance = it }
            }
    }
}
