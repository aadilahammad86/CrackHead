package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.CooldownActivity
import com.example.data.CrackheadRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CrackheadAccessibilityService : AccessibilityService() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private lateinit var repository: CrackheadRepository

    override fun onCreate() {
        super.onCreate()
        instance = this
        repository = CrackheadRepository(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val pkgName = event.packageName?.toString() ?: return
            currentForegroundPackage = pkgName
            checkPackageState(pkgName)
        }
    }

    private fun checkPackageState(pkgName: String) {
        serviceScope.launch {
            val app = repository.appDao.getAppByPackage(pkgName) ?: return@launch
            if (app.isBlocked) {
                if (!app.isCooldownExpired) {
                    // Redirect user immediately to Home screen
                    performGlobalAction(GLOBAL_ACTION_HOME)

                    // Launch Cooldown screen overlay
                    val intent = Intent(this@CrackheadAccessibilityService, CooldownActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        putExtra("PACKAGE_NAME", app.packageName)
                        putExtra("APP_NAME", app.appName)
                        putExtra("COOLDOWN_MINUTES", app.cooldownDurationMinutes)
                        putExtra("REMAINING_SECONDS", app.remainingCooldownSeconds)
                    }
                    startActivity(intent)
                } else {
                    repository.unblockApp(app.packageName)
                }
            }
        }
    }

    override fun onInterrupt() {
        // Accessibility service interrupted
    }

    override fun onDestroy() {
        if (instance == this) instance = null
        serviceJob.cancel()
        super.onDestroy()
    }

    companion object {
        @Volatile
        var instance: CrackheadAccessibilityService? = null

        @Volatile
        var currentForegroundPackage: String? = null
    }
}
