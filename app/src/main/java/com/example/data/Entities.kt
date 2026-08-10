package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monitored_apps")
data class MonitoredApp(
    @PrimaryKey val packageName: String,
    val appName: String,
    val category: String, // "Social", "Video", "Games", "Productive"
    val iconInitials: String = appName.take(2).uppercase(),
    val dailyLimitMinutes: Int = 60,
    val sessionLimitMinutes: Int = 15,
    val dailyUsageSeconds: Long = 0,
    val currentSessionSeconds: Long = 0,
    val lastUsedTimestamp: Long = System.currentTimeMillis(),
    val isMonitored: Boolean = true,
    val isBlocked: Boolean = false,
    val cooldownStartTimestamp: Long = 0,
    val cooldownDurationMinutes: Int = 60
) {
    val remainingCooldownSeconds: Long
        get() {
            if (!isBlocked) return 0
            val elapsedSec = (System.currentTimeMillis() - cooldownStartTimestamp) / 1000
            val totalSec = cooldownDurationMinutes * 60L
            val remain = totalSec - elapsedSec
            return if (remain > 0) remain else 0
        }

    val isCooldownExpired: Boolean
        get() = isBlocked && remainingCooldownSeconds <= 0
}

@Entity(tableName = "usage_rules")
data class UsageRule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val appPackages: List<String>,
    val limitType: String, // "DAILY_TOTAL" or "SINGLE_SESSION"
    val limitMinutes: Int, // e.g. 60
    val dailyLimitMinutes: Int = 60,
    val sessionLimitMinutes: Int = 15,
    val combineMode: String = "OR", // "OR" (either app) or "AND" (both apps)
    val graceWarningEnabled: Boolean = true,
    val cooldownMinutes: Int = 60,
    val isEnabled: Boolean = true
)

@Entity(tableName = "block_logs")
data class BlockLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val reason: String, // "Session limit reached", "Daily total limit exceeded"
    val cooldownMinutes: Int
)

@Entity(tableName = "daily_summary")
data class DailySummary(
    @PrimaryKey val dateString: String, // e.g., "2026-08-08"
    val totalScreenTimeSeconds: Long = 0,
    val blocksTriggered: Int = 0,
    val appsLimitedCount: Int = 0,
    val streakDays: Int = 1
)
