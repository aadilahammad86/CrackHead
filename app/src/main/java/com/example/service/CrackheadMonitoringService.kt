package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
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

    private fun startMonitoringLoop() {
        serviceScope.launch {
            repository.initializeDefaultDataIfEmpty(this@CrackheadMonitoringService)
            while (isActive) {
                checkAndUpdateAppLimits()
                delay(3000) // Check every 3 seconds
            }
        }
    }

    private suspend fun checkAndUpdateAppLimits() {
        val monitoredApps = repository.monitoredApps.first()
        val rules = repository.ruleDao.getActiveRules()

        var blockedCount = 0
        var activeCooldownText = ""

        for (app in monitoredApps) {
            // Check if cooldown expired
            if (app.isBlocked) {
                if (app.isCooldownExpired) {
                    repository.unblockApp(app.packageName)
                } else {
                    blockedCount++
                    val remainingMins = (app.remainingCooldownSeconds / 60) + 1
                    activeCooldownText = "${app.appName} on cooldown (${remainingMins}m remaining)"
                }
            } else {
                // Evaluate rules for this app
                evaluateRulesForApp(app, rules)
            }
        }

        // Update ongoing notification text
        val notificationText = if (activeCooldownText.isNotEmpty()) {
            activeCooldownText
        } else {
            "Watching ${monitoredApps.size} apps • Self-control active"
        }
        updateForegroundNotification(notificationText)
    }

    private suspend fun evaluateRulesForApp(app: MonitoredApp, rules: List<UsageRule>) {
        val appRules = rules.filter { it.appPackages.contains(app.packageName) }
        val defaultGrace = com.example.data.ThemePreferences(this).defaultGraceMinutes
        for (rule in appRules) {
            val limitSec = rule.limitMinutes * 60L
            val warningMins = if (rule.limitMinutes <= defaultGrace) 1 else defaultGrace
            val graceSec = (rule.limitMinutes - warningMins).coerceAtLeast(0) * 60L

            if (rule.limitType == "DAILY_TOTAL") {
                // Check grace warning
                if (rule.graceWarningEnabled && app.dailyUsageSeconds in graceSec until limitSec) {
                    val remMins = ((limitSec - app.dailyUsageSeconds) / 60L).toInt().coerceAtLeast(1)
                    sendGraceNotification(app.appName, remMins)
                }

                // Check limit exceeded
                if (app.dailyUsageSeconds >= limitSec) {
                    repository.triggerBlock(
                        packageName = app.packageName,
                        appName = app.appName,
                        reason = "Daily limit reached (${rule.limitMinutes}m)",
                        cooldownMinutes = rule.cooldownMinutes
                    )
                    launchCooldownScreen(app.packageName, app.appName, rule.cooldownMinutes)
                    break
                }
            } else if (rule.limitType == "SINGLE_SESSION") {
                // Check grace warning for session
                if (rule.graceWarningEnabled && app.currentSessionSeconds in graceSec until limitSec) {
                    val remMins = ((limitSec - app.currentSessionSeconds) / 60L).toInt().coerceAtLeast(1)
                    sendGraceNotification(app.appName, remMins)
                }

                if (app.currentSessionSeconds >= limitSec) {
                    repository.triggerBlock(
                        packageName = app.packageName,
                        appName = app.appName,
                        reason = "Session limit exceeded (${rule.limitMinutes}m sitting)",
                        cooldownMinutes = rule.cooldownMinutes
                    )
                    launchCooldownScreen(app.packageName, app.appName, rule.cooldownMinutes)
                    break
                }
            }
        }
    }

    private fun sendGraceNotification(appName: String, minutesRemaining: Int) {
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
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(NOTIFICATION_WARNING_ID, notification)
    }

    private fun launchCooldownScreen(packageName: String, appName: String, cooldownMinutes: Int) {
        val intent = Intent(this, CooldownActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("PACKAGE_NAME", packageName)
            putExtra("APP_NAME", appName)
            putExtra("COOLDOWN_MINUTES", cooldownMinutes)
        }
        startActivity(intent)
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
