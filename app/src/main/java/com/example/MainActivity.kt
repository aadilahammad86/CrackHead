package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MonitoredApp
import com.example.data.UsageRule
import com.example.ui.MainViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.NewRuleScreen
import com.example.ui.screens.RulesScreen
import com.example.ui.screens.SelectAppsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AccentLavender
import com.example.ui.theme.AccentViolet
import com.example.ui.theme.CooldownRed
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.CrackheadTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.ui.screens.PermissionRequestScreen
import com.example.util.PermissionUtils

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val requestedTab = kotlinx.coroutines.flow.MutableStateFlow<String?>("home")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        val initialTab = intent.getStringExtra("OPEN_TAB") ?: "home"
        requestedTab.value = initialTab

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val colorSchemeSource by viewModel.colorSchemeSource.collectAsState()

            CrackheadTheme(
                themeMode = themeMode,
                colorSchemeSource = colorSchemeSource
            ) {
                CrackheadMainApp(
                    viewModel = viewModel,
                    requestedTabFlow = requestedTab
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val tab = intent.getStringExtra("OPEN_TAB") ?: "home"
        requestedTab.value = tab
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrackheadMainApp(
    viewModel: MainViewModel,
    requestedTabFlow: kotlinx.coroutines.flow.StateFlow<String?>
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var permissionsGranted by remember { mutableStateOf(PermissionUtils.areAllRequiredPermissionsGranted(context)) }
    var userDismissedPermissionScreen by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val isNowGranted = PermissionUtils.areAllRequiredPermissionsGranted(context)
                permissionsGranted = isNowGranted
                if (isNowGranted) {
                    userDismissedPermissionScreen = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (!permissionsGranted && !userDismissedPermissionScreen) {
        PermissionRequestScreen(
            onRequestNotificationPermission = {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    (context as? ComponentActivity)?.requestPermissions(
                        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                        1001
                    )
                }
            },
            onContinueToApp = {
                permissionsGranted = PermissionUtils.areAllRequiredPermissionsGranted(context)
                userDismissedPermissionScreen = true
            },
            onSkipForNow = {
                userDismissedPermissionScreen = true
            }
        )
        return
    }

    val requestedTabState by requestedTabFlow.collectAsState()

    var currentScreen by remember { mutableStateOf(requestedTabState ?: "home") }
    var previousScreen by remember { mutableStateOf("home") }

    androidx.compose.runtime.LaunchedEffect(requestedTabState) {
        requestedTabState?.let { tab ->
            currentScreen = tab
        }
    }
    var selectedAppForSheet by remember { mutableStateOf<MonitoredApp?>(null) }
    var editingRule by remember { mutableStateOf<UsageRule?>(null) }

    val monitoredApps by viewModel.monitoredApps.collectAsState()
    val allApps by viewModel.allApps.collectAsState()
    val rules by viewModel.rules.collectAsState()
    val logs by viewModel.recentLogs.collectAsState()
    val todaySummary by viewModel.todaySummary.collectAsState()
    val selectedRuleApps by viewModel.selectedRuleApps.collectAsState()

    val sheetState = rememberModalBottomSheetState()

    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
    val appBgColor = androidx.compose.material3.MaterialTheme.colorScheme.background

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = appBgColor,
        bottomBar = {
            if (currentScreen in listOf("home", "rules", "insights", "settings")) {
                CrackheadBottomBar(
                    currentScreen = currentScreen,
                    onScreenSelected = { currentScreen = it }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                "home" -> HomeScreen(
                    monitoredApps = monitoredApps,
                    summary = todaySummary,
                    onNavigateToSettings = { currentScreen = "settings" },
                    onNavigateToManageApps = {
                        previousScreen = "home"
                        currentScreen = "select_apps"
                    },
                    onNavigateToNewRule = {
                        editingRule = null
                        previousScreen = "home"
                        val defaultPkgs = monitoredApps.map { it.packageName }.take(2).ifEmpty { allApps.map { it.packageName }.take(2) }
                        viewModel.setSelectedRuleApps(defaultPkgs)
                        currentScreen = "select_apps"
                    },
                    onAppClick = { app -> selectedAppForSheet = app }
                )

                "rules" -> RulesScreen(
                    rules = rules,
                    allApps = allApps,
                    onToggleRule = { updatedRule -> viewModel.saveRule(updatedRule) },
                    onDeleteRule = { ruleId -> viewModel.deleteRule(ruleId) },
                    onEditRule = { rule ->
                        editingRule = rule
                        viewModel.setSelectedRuleApps(rule.appPackages)
                        currentScreen = "new_rule"
                    },
                    onNewRule = {
                        editingRule = null
                        previousScreen = "rules"
                        val defaultPkgs = monitoredApps.map { it.packageName }.take(2).ifEmpty { allApps.map { it.packageName }.take(2) }
                        viewModel.setSelectedRuleApps(defaultPkgs)
                        currentScreen = "select_apps"
                    }
                )

                "select_apps" -> SelectAppsScreen(
                    allApps = allApps,
                    selectedPackages = selectedRuleApps,
                    existingRules = rules,
                    editingRuleId = editingRule?.id,
                    onTogglePackage = { pkg -> viewModel.toggleAppInRuleSelection(pkg) },
                    onRefreshApps = { viewModel.refreshDeviceApps() },
                    onBack = { currentScreen = if (editingRule != null) "new_rule" else previousScreen },
                    onContinue = { currentScreen = "new_rule" }
                )

                "new_rule" -> NewRuleScreen(
                    allApps = allApps,
                    selectedPackages = selectedRuleApps,
                    existingRules = rules,
                    editingRule = editingRule,
                    onOpenAppPicker = { currentScreen = "select_apps" },
                    onBack = {
                        editingRule = null
                        currentScreen = previousScreen
                    },
                    onSaveRule = { rule ->
                        viewModel.saveRule(rule)
                        editingRule = null
                        currentScreen = previousScreen
                    }
                )

                "insights" -> InsightsScreen(
                    summary = todaySummary,
                    logs = logs
                )

                "settings" -> {
                    val themeMode by viewModel.themeMode.collectAsState()
                    val colorSchemeSource by viewModel.colorSchemeSource.collectAsState()
                    val defaultGraceMinutes by viewModel.defaultGraceMinutes.collectAsState()

                    SettingsScreen(
                        themeMode = themeMode,
                        colorSchemeSource = colorSchemeSource,
                        defaultGraceMinutes = defaultGraceMinutes,
                        onThemeModeSelected = { viewModel.setThemeMode(it, isSystemDark) },
                        onColorSchemeSourceSelected = { viewModel.setColorSchemeSource(it, isSystemDark) },
                        onDefaultGraceMinutesChanged = { viewModel.setDefaultGraceMinutes(it) },
                        onTriggerTestBlock = {
                            val targetApp = monitoredApps.firstOrNull() ?: allApps.firstOrNull()
                            if (targetApp != null) {
                                viewModel.triggerTestBlock(
                                    packageName = targetApp.packageName,
                                    appName = targetApp.appName,
                                    cooldownMinutes = 60
                                )
                            }
                        },
                        onSimulateUsage = {
                            val targetApp = monitoredApps.firstOrNull() ?: allApps.firstOrNull()
                            if (targetApp != null) {
                                viewModel.simulateAppUsage(targetApp.packageName, 15)
                            }
                        },
                        onUnblockAll = {
                            monitoredApps.filter { it.isBlocked }.forEach {
                                viewModel.unblockApp(it.packageName)
                            }
                        }
                    )
                }
            }
        }

        // App Detail Action Sheet
        selectedAppForSheet?.let { app ->
            ModalBottomSheet(
                onDismissRequest = { selectedAppForSheet = null },
                sheetState = sheetState,
                containerColor = SurfaceContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val initialsBg = com.example.ui.theme.getAppColor(app.packageName, app.appName)
                    val displayInitials = com.example.ui.theme.getAppInitials(app.appName)

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(initialsBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayInitials,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = app.appName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Category: ${app.category} • Usage today: ${(app.dailyUsageSeconds / 60)}m",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (app.isBlocked) {
                        Button(
                            onClick = {
                                viewModel.unblockApp(app.packageName)
                                selectedAppForSheet = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentViolet,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Unblock App Now", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.triggerTestBlock(app.packageName, app.appName, 60)
                                selectedAppForSheet = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.error,
                                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Trigger 1-Hour Cooldown")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                viewModel.simulateAppUsage(app.packageName, 10)
                                selectedAppForSheet = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simulate +10m Usage", color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

@Composable
fun CrackheadBottomBar(
    currentScreen: String,
    onScreenSelected: (String) -> Unit,
    containerColor: androidx.compose.ui.graphics.Color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer
) {
    val items = listOf(
        BottomNavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("rules", "Rules", Icons.Filled.List, Icons.Outlined.List),
        BottomNavItem("insights", "Insights", Icons.Filled.Analytics, Icons.Outlined.Analytics),
        BottomNavItem("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    NavigationBar(
        containerColor = containerColor,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentScreen == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onScreenSelected(item.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.activeIcon else item.inactiveIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
)
