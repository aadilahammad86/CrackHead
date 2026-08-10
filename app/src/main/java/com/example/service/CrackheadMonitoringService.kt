package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.CooldownActivity
import com.example.MainActivity
import com.example.data.CrackheadRepository
import com.example.data.MonitoredApp
import com.example.data.UsageRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CrackheadMonitoringService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private lateinit var repository: CrackheadRepository

    override fun onCreate() {
        super.onCreate()
        repository = CrackheadRepository(this)
        createNotificationChannels()
        startForeground(NOTIFICATION_ID, buildForegroundNotification("Monitoring self-control rules active"))
        startMonitoringLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceJob.cancel()
        super.onDestroy()
    }

    private val notifiedGraceApps = mutableMapOf<String, Int>()

    private var lastLoopWasBlocked = false

    private fun startMonitoringLoop() {
        serviceScope.launch {
            repository.initializeDefaultDataIfEmpty(this@CrackheadMonitoringService)
            while (isActive) {
                repository.syncMonitoredAppsWithRules()
                repository.syncRealUsageStats(this@CrackheadMonitoringService)
                val hasBlocked = checkAndUpdateAppLimits()
                lastLoopWasBlocked = hasBlocked
                delay(if (hasBlocked) 1000L else 3000L)
            }
        }
    }

    private var lastKnownForegroundPackage: String? = null

    private fun getForegroundAppPackage(): String? {
        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return lastKnownForegroundPackage
        val time = System.currentTimeMillis()
        val events = usm.queryEvents(time - 60000, time)
        val event = UsageEvents.Event()
        var latestPkg: String? = null
        var maxTime = 0L
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                if (event.timeStamp > maxTime) {
                    maxTime = event.timeStamp
                    latestPkg = event.packageName
                }
            }
        }
        if (latestPkg != null) {
            lastKnownForegroundPackage = latestPkg
        }
        return lastKnownForegroundPackage
    }

    private suspend fun checkAndUpdateAppLimits(): Boolean {
        val monitoredApps = repository.monitoredApps.first()
        val rules = repository.ruleDao.getActiveRules()
        val fgFromAccessibility = CrackheadAccessibilityService.currentForegroundPackage
        val foregroundPkg = fgFromAccessibility ?: getForegroundAppPackage()

        var blockedCount = 0
        var activeCooldownText = ""
        val timeStep = if (lastLoopWasBlocked) 1L else 3L

        for (app in monitoredApps) {
            val isForeground = (foregroundPkg == app.packageName)

            // Update session seconds based on foreground status
            var currentApp = app
            if (isForeground) {
                val updatedSession = app.currentSessionSeconds + timeStep
                val updatedDaily = app.dailyUsageSeconds + timeStep
                repository.appDao.updateSessionUsage(app.packageName, updatedSession)
                repository.appDao.updateDailyUsage(app.packageName, updatedDaily, System.currentTimeMillis())
                currentApp = app.copy(
                    currentSessionSeconds = updatedSession,
                    dailyUsageSeconds = updatedDaily
                )
            } else if (app.currentSessionSeconds > 0) {
                repository.appDao.updateSessionUsage(app.packageName, 0)
                currentApp = app.copy(currentSessionSeconds = 0)
                notifiedGraceApps.remove("${app.packageName}_session_1m")
            }

            // Check if cooldown expired
            if (currentApp.isBlocked) {
                if (currentApp.isCooldownExpired) {
                    repository.unblockApp(currentApp.packageName)
                    notifiedGraceApps.remove(currentApp.packageName)
                    notifiedGraceApps.remove("${currentApp.packageName}_daily_1m")
                    notifiedGraceApps.remove("${currentApp.packageName}_session_1m")
                } else {
                    blockedCount++
                    val remSec = currentApp.remainingCooldownSeconds
                    val mins = remSec / 60
                    val secs = remSec % 60
                    val timeFormatted = if (mins > 0) "${mins}m ${secs}s" else "${secs}s"
                    activeCooldownText = "🔒 ${currentApp.appName} on cooldown ($timeFormatted remaining)"

                    // ENFORCEMENT: If user is in or opening blocked app, kick them out to Home Screen & launch Cooldown activity!
                    if (isForeground) {
                        launchCooldownScreen(currentApp.packageName, currentApp.appName, currentApp.cooldownDurationMinutes)
                    }
                }
            } else {
                // Evaluate rules for this app
                evaluateRulesForApp(currentApp, rules, isForeground)
            }
        }

        // Update ongoing notification text
        val notificationText = if (activeCooldownText.isNotEmpty()) {
            activeCooldownText
        } else {
            "Watching ${monitoredApps.size} apps • Self-control active"
        }
        updateForegroundNotification(notificationText)
        return blockedCount > 0
    }

    private suspend fun evaluateRulesForApp(app: MonitoredApp, rules: List<UsageRule>, isForeground: Boolean) {
        val appRules = rules.filter { it.appPackages.contains(app.packageName) }

        for (rule in appRules) {
            val dailyLimitSec = rule.dailyLimitMinutes * 60L
            val sessionLimitSec = rule.sessionLimitMinutes * 60L

            // Grace warning check:
            // ONLY trigger if the monitored app is actively in foreground (isForeground == true)
            // and nearing its final 1 minute (remaining seconds in 1..60)
            if (rule.graceWarningEnabled && isForeground) {
                val dailyRemainingSec = dailyLimitSec - app.dailyUsageSeconds
                if (dailyRemainingSec in 1..60) {
                    val key = "${app.packageName}_daily_1m"
                    if (notifiedGraceApps[key] != 1) {
                        notifiedGraceApps[key] = 1
                        sendGraceNotification(app.packageName, app.appName, 1)
                    }
                }

                val sessionRemainingSec = sessionLimitSec - app.currentSessionSeconds
                if (sessionRemainingSec in 1..60) {
                    val key = "${app.packageName}_session_1m"
                    if (notifiedGraceApps[key] != 1) {
                        notifiedGraceApps[key] = 1
                        sendGraceNotification(app.packageName, app.appName, 1)
                    }
                }
            }

            // Check if daily total limit exceeded
            if (app.dailyUsageSeconds >= dailyLimitSec) {
                val reason = "Daily limit reached (${rule.dailyLimitMinutes}m)"
                repository.triggerBlock(
                    packageName = app.packageName,
                    appName = app.appName,
                    reason = reason,
                    cooldownMinutes = rule.cooldownMinutes
                )
                sendCooldownTriggerNotification(app.packageName, app.appName, rule.cooldownMinutes, reason)
                if (isForeground) {
                    launchCooldownScreen(app.packageName, app.appName, rule.cooldownMinutes)
                }
                break
            }

            // Check if single session limit exceeded
            if (app.currentSessionSeconds >= sessionLimitSec) {
                val reason = "Session limit exceeded (${rule.sessionLimitMinutes}m sitting)"
                repository.triggerBlock(
                    packageName = app.packageName,
                    appName = app.appName,
                    reason = reason,
                    cooldownMinutes = rule.cooldownMinutes
                )
                sendCooldownTriggerNotification(app.packageName, app.appName, rule.cooldownMinutes, reason)
                if (isForeground) {
                    launchCooldownScreen(app.packageName, app.appName, rule.cooldownMinutes)
                }
                break
            }
        }
    }

    private fun redirectToHomeScreen() {
        try {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(homeIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchCooldownScreen(packageName: String, appName: String, cooldownMinutes: Int) {
        val accessibilityService = CrackheadAccessibilityService.instance
        if (accessibilityService != null) {
            accessibilityService.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME)
        } else {
            redirectToHomeScreen()
        }
        val intent = Intent(this, CooldownActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("PACKAGE_NAME", packageName)
            putExtra("APP_NAME", appName)
            putExtra("COOLDOWN_MINUTES", cooldownMinutes)
            putExtra("REMAINING_SECONDS", cooldownMinutes * 60L)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendGraceNotification(packageName: String, appName: String, minutesRemaining: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_WARNINGS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Wrap it up! ⏳")
            .setContentText("You have ~$minutesRemaining min left on $appName before cooldown kicks in.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notifId = NOTIFICATION_WARNING_ID + (packageName.hashCode() and 0xFFFF)
        manager.notify(notifId, notification)
    }

    private fun sendCooldownTriggerNotification(packageName: String, appName: String, cooldownMinutes: Int, reason: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_WARNINGS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🔒 $appName Cooldown Started")
            .setContentText("$reason. Cooldown duration: $cooldownMinutes min.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notifId = NOTIFICATION_WARNING_ID + 1000 + (packageName.hashCode() and 0xFFFF)
        manager.notify(notifId, notification)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val fgsChannel = NotificationChannel(
                CHANNEL_MONITOR,
                "Crackhead Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows persistent self-control status and cooldown timers"
            }

            val warningChannel = NotificationChannel(
                CHANNEL_WARNINGS,
                "Grace Warnings",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when approaching daily or session screen time limits"
            }

            manager.createNotificationChannel(fgsChannel)
            manager.createNotificationChannel(warningChannel)
        }
    }

    private fun buildForegroundNotification(content: String): android.app.Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_MONITOR)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("Crackhead Digital Self-Control")
            .setContentText(content)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun updateForegroundNotification(content: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildForegroundNotification(content))
    }

    companion object {
        const val CHANNEL_MONITOR = "crackhead_monitor_channel"
        const val CHANNEL_WARNINGS = "crackhead_warnings_channel"
        const val NOTIFICATION_ID = 1001
        const val NOTIFICATION_WARNING_ID = 1002
    }
}
