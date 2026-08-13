package com.example.ui

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BlockLog
import com.example.data.CrackheadRepository
import com.example.data.DailySummary
import com.example.data.MonitoredApp
import com.example.data.UsageRule
import com.example.service.CrackheadMonitoringService
import com.example.data.ThemePreferences
import com.example.ui.theme.ColorSchemeSource
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val monitoredApps: List<MonitoredApp> = emptyList(),
    val allApps: List<MonitoredApp> = emptyList(),
    val rules: List<UsageRule> = emptyList(),
    val recentLogs: List<BlockLog> = emptyList(),
    val dailySummary: DailySummary? = null,
    val selectedAppPackagesForRule: List<String> = emptyList(),
    val isServiceRunning: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CrackheadRepository(application)
    private val themePreferences = ThemePreferences(application)

    private val _themeMode = MutableStateFlow(themePreferences.themeMode)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _colorSchemeSource = MutableStateFlow(themePreferences.colorSchemeSource)
    val colorSchemeSource: StateFlow<ColorSchemeSource> = _colorSchemeSource.asStateFlow()

    private val _defaultGraceMinutes = MutableStateFlow(themePreferences.defaultGraceMinutes)
    val defaultGraceMinutes: StateFlow<Int> = _defaultGraceMinutes.asStateFlow()

    val monitoredApps: StateFlow<List<MonitoredApp>> = repository.monitoredApps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allApps: StateFlow<List<MonitoredApp>> = repository.allApps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rules: StateFlow<List<UsageRule>> = repository.allRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentLogs: StateFlow<List<BlockLog>> = repository.recentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todaySummary: StateFlow<DailySummary?> = repository.getTodaySummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _selectedRuleApps = MutableStateFlow<List<String>>(emptyList())
    val selectedRuleApps = _selectedRuleApps.asStateFlow()

    init {
        if (_themeMode.value == ThemeMode.SYSTEM && _colorSchemeSource.value != ColorSchemeSource.DYNAMIC) {
            setColorSchemeSource(ColorSchemeSource.DYNAMIC)
        }
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty(getApplication())
            startMonitoringService()
        }
    }

    fun refreshDeviceApps() {
        viewModelScope.launch {
            repository.syncInstalledApps(getApplication())
            repository.syncRealUsageStats(getApplication())
        }
    }

    fun setThemeMode(mode: ThemeMode, isSystemDark: Boolean = true) {
        themePreferences.themeMode = mode
        _themeMode.value = mode
        if (mode == ThemeMode.SYSTEM) {
            setColorSchemeSource(ColorSchemeSource.DYNAMIC, isSystemDark)
        }
    }

    fun setColorSchemeSource(source: ColorSchemeSource, isSystemDark: Boolean = true) {
        themePreferences.colorSchemeSource = source
        _colorSchemeSource.value = source
        if (source == ColorSchemeSource.STATIC && _themeMode.value == ThemeMode.SYSTEM) {
            val targetMode = if (isSystemDark) ThemeMode.DARK else ThemeMode.LIGHT
            themePreferences.themeMode = targetMode
            _themeMode.value = targetMode
        }
    }

    fun setDefaultGraceMinutes(minutes: Int) {
        themePreferences.defaultGraceMinutes = minutes
        _defaultGraceMinutes.value = minutes
    }

    fun startMonitoringService() {
        val context = getApplication<Application>()
        val intent = Intent(context, CrackheadMonitoringService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun toggleAppMonitored(packageName: String, isMonitored: Boolean) {
        viewModelScope.launch {
            repository.toggleAppMonitored(packageName, isMonitored)
        }
    }

    fun setSelectedRuleApps(packages: List<String>) {
        _selectedRuleApps.value = packages
    }

    fun toggleAppInRuleSelection(packageName: String) {
        val current = _selectedRuleApps.value.toMutableList()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        _selectedRuleApps.value = current
    }

    fun clearSelectedRuleApps() {
        _selectedRuleApps.value = emptyList()
    }

    fun saveRule(rule: UsageRule) {
        viewModelScope.launch {
            repository.saveRule(rule)
        }
    }

    fun deleteRule(ruleId: Long) {
        viewModelScope.launch {
            repository.deleteRule(ruleId)
        }
    }

    fun triggerTestBlock(packageName: String, appName: String, cooldownMinutes: Int = 60) {
        viewModelScope.launch {
            repository.triggerBlock(
                packageName = packageName,
                appName = appName,
                reason = "Manual test cooldown block",
                cooldownMinutes = cooldownMinutes
            )
        }
    }

    fun unblockApp(packageName: String) {
        viewModelScope.launch {
            repository.unblockApp(packageName)
        }
    }

    fun simulateAppUsage(packageName: String, minutes: Int) {
        viewModelScope.launch {
            repository.updateAppUsage(packageName, minutes * 60L, minutes * 60L)
        }
    }
}
