package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MonitoredAppDao {
    @Query("SELECT * FROM monitored_apps ORDER BY appName ASC")
    fun getAllApps(): Flow<List<MonitoredApp>>

    @Query("SELECT * FROM monitored_apps WHERE isMonitored = 1 ORDER BY dailyUsageSeconds DESC")
    fun getMonitoredApps(): Flow<List<MonitoredApp>>

    @Query("SELECT * FROM monitored_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getAppByPackage(packageName: String): MonitoredApp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateApps(apps: List<MonitoredApp>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateApp(app: MonitoredApp)

    @Query("DELETE FROM monitored_apps WHERE packageName = :packageName")
    suspend fun deleteAppByPackage(packageName: String)

    @Query("UPDATE monitored_apps SET isMonitored = :isMonitored WHERE packageName = :packageName")
    suspend fun setMonitored(packageName: String, isMonitored: Boolean)

    @Query("UPDATE monitored_apps SET isBlocked = :isBlocked, cooldownStartTimestamp = :startTimestamp, cooldownDurationMinutes = :durationMinutes WHERE packageName = :packageName")
    suspend fun setBlockedStatus(packageName: String, isBlocked: Boolean, startTimestamp: Long, durationMinutes: Int)

    @Query("UPDATE monitored_apps SET dailyUsageSeconds = :usageSeconds, lastUsedTimestamp = :timestamp WHERE packageName = :packageName")
    suspend fun updateDailyUsage(packageName: String, usageSeconds: Long, timestamp: Long)

    @Query("UPDATE monitored_apps SET currentSessionSeconds = :sessionSeconds WHERE packageName = :packageName")
    suspend fun updateSessionUsage(packageName: String, sessionSeconds: Long)
}

@Dao
interface UsageRuleDao {
    @Query("SELECT * FROM usage_rules ORDER BY id DESC")
    fun getAllRules(): Flow<List<UsageRule>>

    @Query("SELECT * FROM usage_rules WHERE isEnabled = 1")
    suspend fun getActiveRules(): List<UsageRule>

    @Query("SELECT * FROM usage_rules WHERE id = :ruleId LIMIT 1")
    suspend fun getRuleById(ruleId: Long): UsageRule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: UsageRule): Long

    @Update
    suspend fun updateRule(rule: UsageRule)

    @Delete
    suspend fun deleteRule(rule: UsageRule)

    @Query("DELETE FROM usage_rules WHERE id = :ruleId")
    suspend fun deleteRuleById(ruleId: Long)
}

@Dao
interface BlockLogDao {
    @Query("SELECT * FROM block_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<BlockLog>>

    @Insert
    suspend fun insertLog(log: BlockLog)

    @Query("SELECT COUNT(*) FROM block_logs WHERE timestamp >= :sinceTimestamp")
    suspend fun getBlockCountSince(sinceTimestamp: Long): Int
}

@Dao
interface DailySummaryDao {
    @Query("SELECT * FROM daily_summary WHERE dateString = :dateStr LIMIT 1")
    fun getSummaryForDate(dateStr: String): Flow<DailySummary?>

    @Query("SELECT * FROM daily_summary WHERE dateString = :dateStr LIMIT 1")
    suspend fun getSummarySync(dateStr: String): DailySummary?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSummary(summary: DailySummary)
}
