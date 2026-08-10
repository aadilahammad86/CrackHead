package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [MonitoredApp::class, UsageRule::class, BlockLog::class, DailySummary::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CrackheadDatabase : RoomDatabase() {
    abstract fun appDao(): MonitoredAppDao
    abstract fun ruleDao(): UsageRuleDao
    abstract fun logDao(): BlockLogDao
    abstract fun summaryDao(): DailySummaryDao

    companion object {
        @Volatile
        private var INSTANCE: CrackheadDatabase? = null

        fun getDatabase(context: Context): CrackheadDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CrackheadDatabase::class.java,
                    "crackhead_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
