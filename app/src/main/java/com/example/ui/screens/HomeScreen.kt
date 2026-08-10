package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailySummary
import com.example.data.MonitoredApp
import com.example.ui.theme.AccentLavender
import com.example.ui.theme.AccentViolet
import com.example.ui.theme.CardGlowPrimaryEnd
import com.example.ui.theme.CardGlowPrimaryStart
import com.example.ui.theme.CooldownRed
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    monitoredApps: List<MonitoredApp>,
    summary: DailySummary?,
    onNavigateToSettings: () -> Unit,
    onNavigateToManageApps: () -> Unit,
    onNavigateToNewRule: () -> Unit,
    onAppClick: (MonitoredApp) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCanvas)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }

            // Header Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Focus Dashboard",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Crackhead",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextPrimary
                        )
                    }
                }
            }

            // Hero Screen Time Card
            item {
                val totalUsedSec = if (monitoredApps.isEmpty()) 0L else (summary?.totalScreenTimeSeconds ?: 0L)
                val usedHours = totalUsedSec / 3600
                val usedMins = (totalUsedSec % 3600) / 60
                val totalLimitMins = monitoredApps.sumOf { it.dailyLimitMinutes }.coerceAtLeast(60)
                val limitHours = (totalLimitMins + 59) / 60
                val percent = if (monitoredApps.isEmpty() || totalLimitMins == 0) 0 else ((totalUsedSec.toFloat() / (totalLimitMins * 60f)) * 100).toInt().coerceIn(0, 100)

                val heroBgStart = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                val heroBgEnd = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(heroBgStart, heroBgEnd)
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "SCREEN TIME TODAY",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "${usedHours}h ${usedMins}m",
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = " / ${limitHours}h limit",
                                            fontSize = 15.sp,
                                            color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                            modifier = Modifier.padding(bottom = 4.dp, start = 6.dp)
                                        )
                                    }
                                }

                                // Circular Percentage Indicator
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    CircularProgressIndicator(
                                        progress = { percent / 100f },
                                        modifier = Modifier.fillMaxSize(),
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                        trackColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                                        strokeWidth = 6.dp
                                    )
                                    Text(
                                        text = "$percent%",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Linear Progress Bar
                            LinearProgressIndicator(
                                progress = { (percent / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                trackColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            // Stat Cards Grid Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        value = "${summary?.blocksTriggered ?: 0}",
                        label = "Blocks today",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = "${summary?.streakDays ?: 1}d",
                        label = "Streak",
                        valueColor = com.example.ui.theme.SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = "${monitoredApps.size}",
                        label = "Apps limited",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Your Apps Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Monitored Apps",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    TextButton(onClick = onNavigateToManageApps) {
                        Text(
                            text = "Manage Apps",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentViolet
                        )
                    }
                }
            }

            // Monitored Apps Cards
            if (monitoredApps.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToManageApps() },
                        shape = RoundedCornerShape(18.dp),
                        color = SurfaceContainer
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Apps Monitored Yet",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap here or 'Manage Apps' to select apps on your device to track & limit.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(monitoredApps, key = { it.packageName }) { app ->
                    AppUsageCard(
                        app = app,
                        onClick = { onAppClick(app) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // Floating Action Button to Add New Rule
        FloatingActionButton(
            onClick = onNavigateToNewRule,
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "New Rule")
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    valueColor: Color = TextPrimary,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = SurfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun AppUsageCard(
    app: MonitoredApp,
    onClick: () -> Unit
) {
    val usedMins = (app.dailyUsageSeconds / 60).toInt()
    val limitMins = minOf(app.dailyLimitMinutes, app.sessionLimitMinutes).coerceAtLeast(1)
    val progress = (usedMins.toFloat() / limitMins.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "appUsageProgress")

    val initialsBg = com.example.ui.theme.getAppColor(app.packageName, app.appName)
    val displayInitials = com.example.ui.theme.getAppInitials(app.appName)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Icon Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(initialsBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayInitials,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.appName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (app.sessionLimitMinutes <= app.dailyLimitMinutes) {
                            "Limit: ${app.sessionLimitMinutes}m session"
                        } else {
                            "Limit: ${app.dailyLimitMinutes}m daily"
                        },
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // Status String
                if (app.isBlocked) {
                    var remainingSec by remember(app.cooldownStartTimestamp, app.cooldownDurationMinutes) {
                        mutableLongStateOf(app.remainingCooldownSeconds)
                    }

                    LaunchedEffect(app.isBlocked, app.cooldownStartTimestamp) {
                        while (app.isBlocked) {
                            remainingSec = app.remainingCooldownSeconds
                            delay(1000L)
                        }
                    }

                    val mins = remainingSec / 60
                    val secs = remainingSec % 60
                    val timeFormatted = if (mins > 0) "${mins}m ${secs}s" else "${secs}s"

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "On cooldown",
                            tint = CooldownRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "On cooldown ($timeFormatted)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CooldownRed
                        )
                    }
                } else {
                    Text(
                        text = "${usedMins}m / ${limitMins}m",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { if (app.isBlocked) 1f else animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (app.isBlocked) CooldownRed else if (progress > 0.8f) com.example.ui.theme.WarningAmber else AccentViolet,
                trackColor = SurfaceContainerHigh
            )
        }
    }
}
