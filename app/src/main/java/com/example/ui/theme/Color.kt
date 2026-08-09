package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Energetic Vibrant Palette Base Raw Colors
val NeonPurple = Color(0xFF8A00FF)
val NeonPurpleVariant = Color(0xFF6C00C7)
val ElectricBlue = Color(0xFF00E5FF)
val ElectricBlueVariant = Color(0xFF00B4D8)
val VividLime = Color(0xFFA6FF00)
val VividLimeVariant = Color(0xFF76FF03)

val CooldownRed = Color(0xFFFF2A55)
val CooldownRedBg = Color(0xFF3B121A)
val WarningAmber = Color(0xFFFFB74D)
val SuccessGreen = VividLime

val CardGlowPrimaryStart = Color(0xFF533198)
val CardGlowPrimaryEnd = Color(0xFF2D1A54)

// Dynamic Color Scheme Bridges
// Reading these inside @Composable functions will automatically return the active theme's colors!
val DarkCanvas: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background

val SurfaceContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val SurfaceContainerHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceVariant

val SurfaceContainerHighest: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceVariant

val AccentViolet: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primary

val AccentVioletVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primaryContainer

val AccentLavender: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primaryContainer

val AccentPill: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.secondaryContainer

val FabPurple: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primary

val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onBackground

val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

val OnPrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onPrimaryContainer

val CardBorderColor: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

val AppIconColors = listOf(
    Color(0xFFE53935), // Red
    Color(0xFFD81B60), // Pink
    Color(0xFF8E24AA), // Purple
    Color(0xFF5E35B1), // Deep Purple
    Color(0xFF3949AB), // Indigo
    Color(0xFF1E88E5), // Blue
    Color(0xFF039BE5), // Light Blue
    Color(0xFF00ACC1), // Cyan
    Color(0xFF00897B), // Teal
    Color(0xFF43A047), // Green
    Color(0xFF7CB342), // Light Green
    Color(0xFFFB8C00), // Orange
    Color(0xFFF4511E), // Deep Orange
    Color(0xFF6D4C41), // Brown
    Color(0xFF546E7A), // Blue Grey
    Color(0xFFE1306C), // Instagram Pink
    Color(0xFFFF0000), // YouTube Red
    Color(0xFF1DA1F2)  // Twitter Blue
)

fun getAppColor(packageName: String, appName: String = ""): Color {
    val key = packageName.ifEmpty { appName }
    if (key.isEmpty()) return AppIconColors[0]
    val hash = kotlin.math.abs(key.hashCode())
    return AppIconColors[hash % AppIconColors.size]
}

fun getAppInitials(appName: String): String {
    val words = appName.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        words.size > 1 -> {
            words.mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(3).joinToString("")
        }
        words.size == 1 -> {
            val word = words[0]
            if (word.length >= 2) word.take(2).uppercase() else word.uppercase()
        }
        else -> "AP"
    }
}


