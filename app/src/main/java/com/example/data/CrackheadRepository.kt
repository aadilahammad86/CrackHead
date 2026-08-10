package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrackheadRepository(context: Context) {
    private val appContext = context.applicationContext
    private val db = CrackheadDatabase.getDatabase(appContext)
    val appDao = db.appDao()
    val ruleDao = db.ruleDao()
    val logDao = db.logDao()
    val summaryDao = db.summaryDao()

    val allApps: Flow<List<MonitoredApp>> = appDao.getAllApps()
    val monitoredApps: Flow<List<MonitoredApp>> = appDao.getMonitoredApps()
    val allRules: Flow<List<UsageRule>> = ruleDao.getAllRules()
    val recentLogs: Flow<List<BlockLog>> = logDao.getRecentLogs()

    fun getSystemUsageToday(packageName: String): Long {
        try {
            val usm = appContext.getSystemService(Context.USAGE_STATS_SERVICE) as? android.app.usage.UsageStatsManager ?: return 0L
            val cal = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            val statsMap = usm.queryAndAggregateUsageStats(cal.timeInMillis, System.currentTimeMillis())
            val stat = statsMap?.get(packageName)
            return (stat?.totalTimeInForeground ?: 0L) / 1000L
        } catch (e: Exception) {
            return 0L
        }
    }

    fun getTodaySummary(): Flow<DailySummary?> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return summaryDao.getSummaryForDate(today)
    }

    suspend fun initializeDefaultDataIfEmpty(context: Context) {
        syncInstalledApps(context)
        syncMonitoredAppsWithRules()
        syncRealUsageStats(context)

        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val existingSummary = summaryDao.getSummarySync(todayStr)
        if (existingSummary == null) {
            summaryDao.insertOrUpdateSummary(
                DailySummary(
                    dateString = todayStr,
                    totalScreenTimeSeconds = 0L,
                    blocksTriggered = 0,
                    appsLimitedCount = 0,
                    streakDays = 1
                )
            )
        }
    }

    suspend fun syncInstalledApps(context: Context) {
        try {
            val pm = context.packageManager
            val mainIntent = android.content.Intent(android.content.Intent.ACTION_MAIN, null).apply {
                addCategory(android.content.Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
            val existingAppsMap = allApps.first().associateBy { it.packageName }
            val newAppsList = mutableListOf<MonitoredApp>()
            val installedPackages = mutableSetOf<String>()

            for (info in resolveInfos) {
                val pkg = info.activityInfo.packageName
                if (pkg == context.packageName) continue // Skip our own app
                installedPackages.add(pkg)

                val label = info.loadLabel(pm).toString()
                val initials = com.example.ui.theme.getAppInitials(label)

                val existing = existingAppsMap[pkg]
                if (existing != null) {
                    newAppsList.add(existing.copy(appName = label, iconInitials = initials))
                } else {
                    val category = when {
                        pkg.contains("instagram") || pkg.contains("twitter") || pkg.contains("facebook") || pkg.contains("tiktok") || pkg.contains("social") || pkg.contains("whatsapp") || pkg.contains("telegram") -> "Social"
                        pkg.contains("youtube") || pkg.contains("netflix") || pkg.contains("video") || pkg.contains("media") || pkg.contains("vlc") -> "Video"
                        pkg.contains("game") || pkg.contains("play") -> "Games"
                        pkg.contains("chrome") || pkg.contains("browser") -> "Web"
                        else -> "App"
                    }
                    newAppsList.add(
                        MonitoredApp(
                            packageName = pkg,
                            appName = label,
                            category = category,
                            iconInitials = initials,
                            dailyLimitMinutes = 60,
                            sessionLimitMinutes = 20,
                            dailyUsageSeconds = 0L,
                            isMonitored = false
                        )
                    )
                }
            }

            // Remove any app from DB that is not physically installed on the device
            if (installedPackages.isNotEmpty()) {
                val toRemove = existingAppsMap.values.filter {
                    !installedPackages.contains(it.packageName)
                }
                for (uninstalled in toRemove) {
                    appDao.deleteAppByPackage(uninstalled.packageName)
                }

                // Clean up rules referencing uninstalled packages
                val currentRules = allRules.first()
                for (rule in currentRules) {
                    val validPkgs = rule.appPackages.filter { installedPackages.contains(it) }
                    if (validPkgs.isEmpty()) {
                        ruleDao.deleteRule(rule)
                    } else if (validPkgs.size != rule.appPackages.size) {
                        ruleDao.updateRule(rule.copy(appPackages = validPkgs))
                    }
                }
            }

            if (newAppsList.isNotEmpty()) {
                appDao.insertOrUpdateApps(newAppsList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun syncRealUsageStats(context: Context) {
        try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as? android.app.usage.UsageStatsManager
                ?: return
            val cal = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            val startTime = cal.timeInMillis
            val endTime = System.currentTimeMillis()
            val statsMap = usm.queryAndAggregateUsageStats(startTime, endTime)

            val currentApps = allApps.first()
            var totalScreenTimeSec = 0L

            val updatedApps = currentApps.map { app ->
                val stat = statsMap?.get(app.packageName)
                if (stat != null && stat.totalTimeInForeground > 0) {
                    val systemUsageSec = stat.totalTimeInForeground / 1000L
                    val effectiveBaseline = if (systemUsageSec < app.baselineUsageSeconds) 0L else app.baselineUsageSeconds
                    val monitoredUsage = maxOf(0L, systemUsageSec - effectiveBaseline)
                    if (app.isMonitored) {
                        totalScreenTimeSec += maxOf(app.dailyUsageSeconds, monitoredUsage)
                    }
                    app.copy(
                        baselineUsageSeconds = effectiveBaseline,
                        dailyUsageSeconds = maxOf(app.dailyUsageSeconds, monitoredUsage)
                    )
                } else {
                    if (app.isMonitored) {
                        totalScreenTimeSec += app.dailyUsageSeconds
                    }
                    app
                }
            }
            for (app in updatedApps) {
                val freshApp = appDao.getAppByPackage(app.packageName)
                if (freshApp != null) {
                    val mergedApp = freshApp.copy(
                        baselineUsageSeconds = app.baselineUsageSeconds,
                        dailyUsageSeconds = maxOf(freshApp.dailyUsageSeconds, app.dailyUsageSeconds)
                    )
                    appDao.insertOrUpdateApp(mergedApp)
                }
            }

            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val summary = summaryDao.getSummarySync(todayStr) ?: DailySummary(todayStr)
            summaryDao.insertOrUpdateSummary(
                summary.copy(totalScreenTimeSeconds = totalScreenTimeSec)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun syncMonitoredAppsWithRules() {
        try {
            val rules = ruleDao.getAllRulesList()
            val allAppsList = appDao.getAllAppsList()
            val updatedList = mutableListOf<MonitoredApp>()

            for (app in allAppsList) {
                val appRules = rules.filter { it.isEnabled && it.appPackages.contains(app.packageName) }

                if (appRules.isNotEmpty()) {
                    val newDailyLimit = appRules.map { it.dailyLimitMinutes }.minOrNull()
                        ?: app.dailyLimitMinutes

                    val newSessionLimit = appRules.map { it.sessionLimitMinutes }.minOrNull()
                        ?: app.sessionLimitMinutes

                    val newCooldown = appRules.maxOfOrNull { it.cooldownMinutes } ?: app.cooldownDurationMinutes

                    val updatedApp = app.copy(
                        isMonitored = true,
                        dailyLimitMinutes = newDailyLimit,
                        sessionLimitMinutes = newSessionLimit,
                        cooldownDurationMinutes = newCooldown
                    )
                    updatedList.add(updatedApp)
                } else {
                    if (app.isMonitored) {
                        updatedList.add(app.copy(isMonitored = false))
                    }
                }
            }

            for (updatedApp in updatedList) {
                val freshApp = appDao.getAppByPackage(updatedApp.packageName)
                if (freshApp != null) {
                    val mergedApp = freshApp.copy(
                        isMonitored = updatedApp.isMonitored,
                        dailyLimitMinutes = updatedApp.dailyLimitMinutes,
                        sessionLimitMinutes = updatedApp.sessionLimitMinutes,
                        cooldownDurationMinutes = updatedApp.cooldownDurationMinutes
                    )
                    appDao.insertOrUpdateApp(mergedApp)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun toggleAppMonitored(packageName: String, isMonitored: Boolean) {
        val app = appDao.getAppByPackage(packageName)
        if (app != null) {
            val currentSystemSec = if (isMonitored) getSystemUsageToday(packageName) else app.baselineUsageSeconds
            appDao.insertOrUpdateApp(
                app.copy(
                    isMonitored = isMonitored,
                    dailyUsageSeconds = if (isMonitored) 0L else app.dailyUsageSeconds,
                    currentSessionSeconds = 0L,
                    isBlocked = if (!isMonitored) false else app.isBlocked,
                    cooldownStartTimestamp = if (!isMonitored) 0L else app.cooldownStartTimestamp,
                    baselineUsageSeconds = currentSystemSec
                )
            )
        }
    }

    suspend fun saveRule(rule: UsageRule): Long {
        val id = if (rule.id == 0L) {
            ruleDao.insertRule(rule)
        } else {
            ruleDao.updateRule(rule)
            rule.id
        }

        // Clean slate & baseline offset for all target apps when rule is created or edited
        for (pkg in rule.appPackages) {
            val app = appDao.getAppByPackage(pkg)
            if (app != null) {
                val currentSystemSec = getSystemUsageToday(pkg)
                appDao.insertOrUpdateApp(
                    app.copy(
                        dailyUsageSeconds = 0L,
                        currentSessionSeconds = 0L,
                        isBlocked = false,
                        cooldownStartTimestamp = 0L,
                        baselineUsageSeconds = currentSystemSec
                    )
                )
            }
        }

        syncMonitoredAppsWithRules()

        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentSummary = summaryDao.getSummarySync(todayStr) ?: DailySummary(todayStr)
        val monitoredCount = appDao.getMonitoredApps().first().size
        summaryDao.insertOrUpdateSummary(
            currentSummary.copy(appsLimitedCount = monitoredCount)
        )

        return id
    }

    suspend fun deleteRule(ruleId: Long) {
        ruleDao.deleteRuleById(ruleId)
        syncMonitoredAppsWithRules()
    }

    suspend fun triggerBlock(packageName: String, appName: String, reason: String, cooldownMinutes: Int) {
        val now = System.currentTimeMillis()
        appDao.setBlockedStatus(packageName, true, now, cooldownMinutes)
        logDao.insertLog(
            BlockLog(
                packageName = packageName,
                appName = appName,
                reason = reason,
                cooldownMinutes = cooldownMinutes
            )
        )

        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentSummary = summaryDao.getSummarySync(todayStr) ?: DailySummary(todayStr)
        summaryDao.insertOrUpdateSummary(
            currentSummary.copy(blocksTriggered = currentSummary.blocksTriggered + 1)
        )
    }

    suspend fun unblockApp(packageName: String) {
        appDao.setBlockedStatus(packageName, false, 0, 0)
    }

    suspend fun updateAppUsage(packageName: String, additionalSeconds: Long, sessionSeconds: Long) {
        val app = appDao.getAppByPackage(packageName) ?: return
        val newDailyUsage = app.dailyUsageSeconds + additionalSeconds
        appDao.updateDailyUsage(packageName, newDailyUsage, System.currentTimeMillis())
        appDao.updateSessionUsage(packageName, sessionSeconds)

        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentSummary = summaryDao.getSummarySync(todayStr) ?: DailySummary(todayStr)
        summaryDao.insertOrUpdateSummary(
            currentSummary.copy(totalScreenTimeSeconds = currentSummary.totalScreenTimeSeconds + additionalSeconds)
        )
    }
}
