package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MonitoredApp
import com.example.data.UsageRule
import com.example.ui.theme.AccentLavender
import com.example.ui.theme.AccentViolet
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NewRuleScreen(
    allApps: List<MonitoredApp>,
    selectedPackages: List<String>,
    editingRule: UsageRule? = null,
    onOpenAppPicker: () -> Unit,
    onBack: () -> Unit,
    onSaveRule: (UsageRule) -> Unit
) {
    var limitType by remember(editingRule) { mutableStateOf(editingRule?.limitType ?: "DAILY_TOTAL") } // "DAILY_TOTAL" or "SINGLE_SESSION"
    var limitMinutes by remember(editingRule) { mutableIntStateOf(editingRule?.limitMinutes ?: 60) } // 60 mins (1h 00m)
    var combineMode by remember(editingRule) { mutableStateOf(editingRule?.combineMode ?: "OR") } // "OR" or "AND"
    var graceWarningEnabled by remember(editingRule) { mutableStateOf(editingRule?.graceWarningEnabled ?: true) }
    var cooldownMinutes by remember(editingRule) { mutableIntStateOf(editingRule?.cooldownMinutes ?: 60) }
    var showTimePicker by remember { mutableStateOf(false) }

    val selectedApps = allApps.filter { selectedPackages.contains(it.packageName) }

    val hoursPart = limitMinutes / 60
    val minsPart = limitMinutes % 60
    val timeFormatted = if (hoursPart > 0) "${hoursPart}h ${String.format("%02dm", minsPart)}" else "${minsPart}m"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCanvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (editingRule != null) "Edit Rule" else "New Rule",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // APPLIES TO
            Text(
                text = "APPLIES TO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                selectedApps.forEach { app ->
                    val initialsBg = com.example.ui.theme.getAppColor(app.packageName, app.appName)
                    val displayInitials = com.example.ui.theme.getAppInitials(app.appName)

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(initialsBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = displayInitials,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = app.appName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }
                }

                // Add button chip
                IconButton(
                    onClick = onOpenAppPicker,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add app",
                        tint = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // LIMIT TYPE
            Text(
                text = "LIMIT TYPE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    val dailySelected = limitType == "DAILY_TOTAL"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { limitType = "DAILY_TOTAL" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (dailySelected) androidx.compose.material3.MaterialTheme.colorScheme.primary else Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Daily Total",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (dailySelected) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else TextSecondary
                            )
                        }
                    }

                    val sessionSelected = limitType == "SINGLE_SESSION"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { limitType = "SINGLE_SESSION" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (sessionSelected) androidx.compose.material3.MaterialTheme.colorScheme.primary else Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Single Session",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (sessionSelected) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Duration Picker Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SurfaceContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Allow up to",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { if (limitMinutes > 5) limitMinutes -= 5 },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary)
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerHigh)
                                .clickable { showTimePicker = true }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = timeFormatted,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Tap to pick time",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        IconButton(
                            onClick = { if (limitMinutes < 300) limitMinutes += 5 },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Combine Apps Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SurfaceContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Combine apps",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (combineMode == "OR") "Trigger block when either app hits its limit" else "Trigger block when both apps hit limit together",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerHigh
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { combineMode = "OR" },
                                shape = RoundedCornerShape(10.dp),
                                color = if (combineMode == "OR") androidx.compose.material3.MaterialTheme.colorScheme.primary else Color.Transparent
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "OR (either app)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (combineMode == "OR") androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else TextSecondary
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { combineMode = "AND" },
                                shape = RoundedCornerShape(10.dp),
                                color = if (combineMode == "AND") androidx.compose.material3.MaterialTheme.colorScheme.primary else Color.Transparent
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "AND (both apps)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (combineMode == "AND") androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Grace Warning Card
            val context = LocalContext.current
            val defaultGrace = remember(context) { com.example.data.ThemePreferences(context).defaultGraceMinutes }
            val effectiveGrace = if (limitMinutes <= defaultGrace) 1 else defaultGrace

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SurfaceContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${effectiveGrace}-min grace warning",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Notify $effectiveGrace min${if (effectiveGrace > 1) "s" else ""} before block",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = graceWarningEnabled,
                        onCheckedChange = { graceWarningEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cooldown Period Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SurfaceContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Cooldown period",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Duration app stays locked after hitting limit",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerHigh
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            listOf(15 to "15m", 30 to "30m", 60 to "1h", 120 to "2h", 180 to "3h").forEach { (mins, label) ->
                                val selected = cooldownMinutes == mins
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { cooldownMinutes = mins },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.primary else Color.Transparent
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Rule Button
            Button(
                onClick = {
                    val updatedRule = UsageRule(
                        id = editingRule?.id ?: 0L,
                        name = if (selectedApps.isNotEmpty()) {
                            selectedApps.joinToString(" + ") { it.appName } + " Rule"
                        } else (editingRule?.name ?: "Custom Rule"),
                        appPackages = selectedPackages,
                        limitType = limitType,
                        limitMinutes = limitMinutes,
                        combineMode = combineMode,
                        graceWarningEnabled = graceWarningEnabled,
                        cooldownMinutes = cooldownMinutes,
                        isEnabled = editingRule?.isEnabled ?: true
                    )
                    onSaveRule(updatedRule)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = if (editingRule != null) "Update Rule" else "Save Rule",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        if (showTimePicker) {
            DurationTimePickerDialog(
                initialMinutes = limitMinutes,
                onDismiss = { showTimePicker = false },
                onTimeSelected = { selectedMinutes ->
                    limitMinutes = selectedMinutes
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationTimePickerDialog(
    initialMinutes: Int,
    onDismiss: () -> Unit,
    onTimeSelected: (Int) -> Unit
) {
    val initialHour = (initialMinutes / 60) % 24
    val initialMin = initialMinutes % 60
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMin,
        is24Hour = false
    )
    var isKeyboardInput by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMinutes = timePickerState.hour * 60 + timePickerState.minute
                    onTimeSelected(if (selectedMinutes > 0) selectedMinutes else 5)
                    onDismiss()
                }
            ) {
                Text("OK", fontWeight = FontWeight.Bold, color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
            }
        },
        title = {
            Text(
                text = "Select time",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isKeyboardInput) {
                    TimeInput(state = timePickerState)
                } else {
                    TimePicker(state = timePickerState)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = { isKeyboardInput = !isKeyboardInput }
                    ) {
                        Icon(
                            imageVector = if (isKeyboardInput) Icons.Default.Schedule else Icons.Default.Keyboard,
                            contentDescription = if (isKeyboardInput) "Switch to dial picker" else "Switch to keyboard input",
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(28.dp)
    )
}
