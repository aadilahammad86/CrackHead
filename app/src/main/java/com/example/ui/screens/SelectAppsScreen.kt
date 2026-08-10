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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MonitoredApp
import com.example.ui.theme.AccentLavender
import com.example.ui.theme.AccentViolet
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.LaunchedEffect

import com.example.data.UsageRule
import com.example.ui.dialogs.AppRuleConflict
import com.example.ui.dialogs.RuleConflictDialog

@Composable
fun SelectAppsScreen(
    allApps: List<MonitoredApp>,
    selectedPackages: List<String>,
    existingRules: List<UsageRule> = emptyList(),
    editingRuleId: Long? = null,
    onTogglePackage: (String) -> Unit,
    onRefreshApps: (() -> Unit)? = null,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var conflictDialogState by remember { mutableStateOf<List<AppRuleConflict>?>(null) }

    LaunchedEffect(Unit) {
        onRefreshApps?.invoke()
    }

    val filteredApps = allApps.filter {
        it.appName.contains(searchQuery, ignoreCase = true) ||
        it.category.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCanvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Top Header
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
                    text = "Select Watchlist Apps",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { onRefreshApps?.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Apps from Device",
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search installed apps", color = TextMuted) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceContainerHigh,
                    unfocusedContainerColor = SurfaceContainerHigh,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INSTALLED APPS ON DEVICE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = TextSecondary
                )
                Text(
                    text = "${filteredApps.size} apps found",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // App List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredApps, key = { it.packageName }) { app ->
                    val isSelected = selectedPackages.contains(app.packageName)
                    val existingRuleForApp = existingRules.find { rule ->
                        rule.id != editingRuleId && rule.appPackages.contains(app.packageName)
                    }

                    val initialsBg = com.example.ui.theme.getAppColor(app.packageName, app.appName)
                    val displayInitials = com.example.ui.theme.getAppInitials(app.appName)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (existingRuleForApp != null && !isSelected) {
                                    conflictDialogState = listOf(
                                        AppRuleConflict(
                                            appName = app.appName,
                                            existingRuleName = existingRuleForApp.name,
                                            packageName = app.packageName
                                        )
                                    )
                                } else {
                                    onTogglePackage(app.packageName)
                                }
                            },
                        shape = RoundedCornerShape(18.dp),
                        color = SurfaceContainer
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                Spacer(modifier = Modifier.height(2.dp))
                                if (existingRuleForApp != null) {
                                    Text(
                                        text = "In rule: ${existingRuleForApp.name}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = com.example.ui.theme.WarningAmber
                                    )
                                } else {
                                    Text(
                                        text = app.category,
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Switch(
                                checked = isSelected,
                                onCheckedChange = {
                                    if (existingRuleForApp != null && !isSelected) {
                                        conflictDialogState = listOf(
                                            AppRuleConflict(
                                                appName = app.appName,
                                                existingRuleName = existingRuleForApp.name,
                                                packageName = app.packageName
                                            )
                                        )
                                    } else {
                                        onTogglePackage(app.packageName)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                    uncheckedThumbColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    uncheckedTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Bottom Selected Action Panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "${selectedPackages.size} apps selected",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = {
                        val activeConflicts = selectedPackages.mapNotNull { pkg ->
                            val rule = existingRules.find { r -> r.id != editingRuleId && r.appPackages.contains(pkg) }
                            val app = allApps.find { a -> a.packageName == pkg }
                            if (rule != null && app != null) {
                                AppRuleConflict(appName = app.appName, existingRuleName = rule.name, packageName = pkg)
                            } else null
                        }

                        if (activeConflicts.isNotEmpty()) {
                            conflictDialogState = activeConflicts
                        } else {
                            onContinue()
                        }
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
                        text = if (selectedPackages.isNotEmpty()) "Confirm Selection & Set Rule" else "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        conflictDialogState?.let { conflicts ->
            RuleConflictDialog(
                conflicts = conflicts,
                onDismiss = { conflictDialogState = null }
            )
        }
    }
}
