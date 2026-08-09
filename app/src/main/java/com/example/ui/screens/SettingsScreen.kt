package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentLavender
import com.example.ui.theme.AccentViolet
import com.example.ui.theme.CooldownRed
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SettingsSuggest
import com.example.ui.theme.ColorSchemeSource
import com.example.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    colorSchemeSource: ColorSchemeSource = ColorSchemeSource.STATIC,
    defaultGraceMinutes: Int = 1,
    onThemeModeSelected: (ThemeMode) -> Unit = {},
    onColorSchemeSourceSelected: (ColorSchemeSource) -> Unit = {},
    onDefaultGraceMinutesChanged: (Int) -> Unit = {},
    onTriggerTestBlock: () -> Unit,
    onSimulateUsage: () -> Unit,
    onUnblockAll: () -> Unit
) {
    val context = LocalContext.current
    var strictModeEnabled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCanvas)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
                Text(
                    text = "Settings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Theme & Color Scheme Settings Banner
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceContainer
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = AccentViolet,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Appearance & Theme",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        // Theme Mode Selection
                        Column {
                            Text(
                                text = "Theme Mode",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SurfaceContainerHigh,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                ) {
                                    val modeOptions = listOf(
                                        Triple(ThemeMode.SYSTEM, "System", Icons.Default.SettingsSuggest),
                                        Triple(ThemeMode.DARK, "Dark", Icons.Default.DarkMode),
                                        Triple(ThemeMode.LIGHT, "Light", Icons.Default.LightMode)
                                    )

                                    modeOptions.forEach { (mode, label, icon) ->
                                        val selected = themeMode == mode
                                        val targetBg = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.primary else Color.Transparent
                                        val targetFg = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else TextSecondary
                                        val animatedBgColor by animateColorAsState(
                                            targetValue = targetBg,
                                            animationSpec = tween(durationMillis = 300),
                                            label = "themeModeBg"
                                        )
                                        val animatedFgColor by animateColorAsState(
                                            targetValue = targetFg,
                                            animationSpec = tween(durationMillis = 300),
                                            label = "themeModeFg"
                                        )

                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { onThemeModeSelected(mode) },
                                            shape = RoundedCornerShape(10.dp),
                                            color = animatedBgColor
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                    tint = animatedFgColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = label,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = animatedFgColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Color Scheme Selection
                        Column {
                            Text(
                                text = "Color Scheme",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SurfaceContainerHigh,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                ) {
                                    val schemeOptions = listOf(
                                        Triple(ColorSchemeSource.STATIC, "Static Vibrant", Icons.Default.Palette),
                                        Triple(ColorSchemeSource.DYNAMIC, "Dynamic M3", Icons.Default.AutoAwesome)
                                    )

                                    schemeOptions.forEach { (scheme, label, icon) ->
                                        val selected = colorSchemeSource == scheme
                                        val targetBg = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.primary else Color.Transparent
                                        val targetFg = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else TextSecondary
                                        val animatedBgColor by animateColorAsState(
                                            targetValue = targetBg,
                                            animationSpec = tween(durationMillis = 300),
                                            label = "schemeSourceBg"
                                        )
                                        val animatedFgColor by animateColorAsState(
                                            targetValue = targetFg,
                                            animationSpec = tween(durationMillis = 300),
                                            label = "schemeSourceFg"
                                        )

                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { onColorSchemeSourceSelected(scheme) },
                                            shape = RoundedCornerShape(10.dp),
                                            color = animatedBgColor
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                    tint = animatedFgColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = label,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = animatedFgColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (colorSchemeSource == ColorSchemeSource.DYNAMIC)
                                    "Dynamic color automatically adapts to system wallpaper on Android 12+"
                                else
                                    "Vibrant Palette applies neon purple, electric blue, and vivid lime accents",
                                fontSize = 12.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Default Grace Period Setting Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceContainer
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Default Grace Period",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Advance warning time before limit block",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceContainerHigh,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                listOf(1, 2, 3, 5).forEach { mins ->
                                    val selected = defaultGraceMinutes == mins
                                    val targetBg = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.primary else Color.Transparent
                                    val targetFg = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else TextSecondary
                                    val animatedBgColor by animateColorAsState(
                                        targetValue = targetBg,
                                        animationSpec = tween(durationMillis = 300),
                                        label = "gracePeriodBg"
                                    )
                                    val animatedFgColor by animateColorAsState(
                                        targetValue = targetFg,
                                        animationSpec = tween(durationMillis = 300),
                                        label = "gracePeriodFg"
                                    )

                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onDefaultGraceMinutesChanged(mins) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = animatedBgColor
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${mins}m",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = animatedFgColor
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Currently notifying $defaultGraceMinutes min${if (defaultGraceMinutes > 1) "s" else ""} before a rule limit is reached.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            // Accessibility Permission Banner
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceContainer
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Accessibility,
                                contentDescription = null,
                                tint = AccentViolet,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Accessibility Service",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Required for instant background app tracking and gentle home-screen redirection during active cooldown periods.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Enable Accessibility", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Usage Access Banner
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceContainer
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = AccentViolet,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Usage Stats Permission",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Grants Crackhead permission to accurately measure daily screen time totals across selected apps.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Grant Usage Access", color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // Strict Mode Card
            item {
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
                                text = "Strict Mode",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Prevents deleting rules or lowering limits during active cooldown",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }

                        Switch(
                            checked = strictModeEnabled,
                            onCheckedChange = { strictModeEnabled = it },
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

            // Demo Actions Section
            item {
                Text(
                    text = "Demo & Testing Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceContainer
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onTriggerTestBlock,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.error,
                                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Trigger Test Cooldown Block")
                        }

                        Button(
                            onClick = onSimulateUsage,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simulate +15m App Usage")
                        }

                        OutlinedButton(
                            onClick = onUnblockAll,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Unblock All Apps", color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
