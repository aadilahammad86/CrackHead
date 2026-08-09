package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
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
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ALLOW UP TO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Time Template Chips
                    val presetTemplates = listOf(1, 2, 5, 10, 15, 30, 60, 120)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(presetTemplates) { mins ->
                            val label = when {
                                mins < 60 -> "${mins}m"
                                mins % 60 == 0 -> "${mins / 60}h"
                                else -> "${mins / 60}h ${mins % 60}m"
                            }
                            val isSelected = limitMinutes == mins
                            Surface(
                                onClick = { limitMinutes = mins },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) androidx.compose.material3.MaterialTheme.colorScheme.primary else SurfaceContainerHigh
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { if (limitMinutes > 1) limitMinutes -= 1 },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceContainerHigh)
                                .clickable { showTimePicker = true }
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = timeFormatted,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Wheel Picker",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        IconButton(
                            onClick = { if (limitMinutes < 1440) limitMinutes += 1 },
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
            ExpressiveWheelTimePickerDialog(
                initialMinutes = limitMinutes,
                onDismiss = { showTimePicker = false },
                onTimeSelected = { selectedMinutes ->
                    limitMinutes = selectedMinutes
                }
            )
        }
    }
}

@Composable
fun WheelPickerColumn(
    items: List<Int>,
    selectedItem: Int,
    unitLabel: String,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleCount = 5
    val itemHeight = 44.dp
    val selectedIndex = items.indexOf(selectedItem).coerceAtLeast(0)

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedItem) {
        val targetIdx = items.indexOf(selectedItem).coerceAtLeast(0)
        if (listState.firstVisibleItemIndex != targetIdx && !listState.isScrollInProgress) {
            listState.scrollToItem(targetIdx)
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val centerIndex = listState.firstVisibleItemIndex
            val item = items.getOrNull(centerIndex) ?: items.first()
            if (item != selectedItem) {
                onItemSelected(item)
            }
        }
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleCount)
            .width(100.dp),
        contentAlignment = Alignment.Center
    ) {
        // Center selection highlight box
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight),
            shape = RoundedCornerShape(14.dp),
            color = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                androidx.compose.material3.MaterialTheme.colorScheme.primary
            )
        ) {}

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = itemHeight * 2),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(items) { index, itemValue ->
                val isSelected = itemValue == selectedItem
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .clickable {
                            onItemSelected(itemValue)
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$itemValue $unitLabel",
                        fontSize = if (isSelected) 22.sp else 15.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (isSelected)
                            androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun ExpressiveWheelTimePickerDialog(
    initialMinutes: Int,
    onDismiss: () -> Unit,
    onTimeSelected: (Int) -> Unit
) {
    var selectedHours by remember { mutableIntStateOf((initialMinutes / 60).coerceIn(0, 23)) }
    var selectedMins by remember { mutableIntStateOf(initialMinutes % 60) }

    val presetTemplates = listOf(1, 2, 5, 10, 15, 30, 60, 120)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val totalMins = selectedHours * 60 + selectedMins
                    onTimeSelected(if (totalMins > 0) totalMins else 1)
                    onDismiss()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Confirm", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        title = {
            Column {
                Text(
                    text = "Select Time Limit",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Scroll wheel or pick a quick template",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Quick Presets Row
                Text(
                    text = "QUICK TEMPLATES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextMuted,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    items(presetTemplates) { mins ->
                        val label = when {
                            mins < 60 -> "${mins}m"
                            mins % 60 == 0 -> "${mins / 60}h"
                            else -> "${mins / 60}h ${mins % 60}m"
                        }
                        val isSelected = (selectedHours * 60 + selectedMins) == mins
                        Surface(
                            onClick = {
                                selectedHours = mins / 60
                                selectedMins = mins % 60
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) androidx.compose.material3.MaterialTheme.colorScheme.primary else SurfaceContainerHigh
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else TextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Wheel Columns Container
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceContainerHigh
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hours Wheel
                        WheelPickerColumn(
                            items = (0..23).toList(),
                            selectedItem = selectedHours,
                            unitLabel = "hrs",
                            onItemSelected = { selectedHours = it }
                        )

                        Text(
                            text = ":",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                        )

                        // Minutes Wheel
                        WheelPickerColumn(
                            items = (0..59).toList(),
                            selectedItem = selectedMins,
                            unitLabel = "mins",
                            onItemSelected = { selectedMins = it }
                        )
                    }
                }
            }
        },
        containerColor = SurfaceContainer,
        shape = RoundedCornerShape(28.dp)
    )
}
