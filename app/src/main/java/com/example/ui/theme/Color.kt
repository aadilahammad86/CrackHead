package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

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

// Dynamic Color Scheme Bridges (Delegating to ThemeTokens)
val DarkCanvas: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.AppBackground

val SurfaceContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.Container

val SurfaceContainerHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.ContainerHigh

val SurfaceContainerHighest: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.ContainerHighest

val ExpressiveCardBorder: Color
    @Composable
    @ReadOnlyComposable
    get() {
        val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
        return if (isDark) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
        } else {
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.60f)
        }
    }

val AccentViolet: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.Primary

val AccentVioletVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.PrimaryContainer

val AccentLavender: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.PrimaryContainer

val AccentPill: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.SecondaryContainer

val FabPurple: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.Primary

val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.TextPrimary

val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.TextSecondary

val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.TextMuted

val OnPrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.OnPrimaryContainer

val CardBorderColor: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.CardBorder

// Centralized Semantic State Tokens (Delegated to ThemeTokens)
val StatusGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.Success

val StatusGreenBg: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.SuccessContainer

val StatusGreenBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.SuccessBorder

val StatusGreenContent: Color
    @Composable
    @ReadOnlyComposable
    get() = ThemeTokens.OnSuccessContainer

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

@Composable
fun ExpressiveBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val bg = MaterialTheme.colorScheme.background
    val isDark = bg.luminance() < 0.5f
    
    val primaryAlpha = if (isDark) 0.08f else 0.05f
    val tertiaryAlpha = if (isDark) 0.05f else 0.03f

    val primaryTint = MaterialTheme.colorScheme.primary.copy(alpha = primaryAlpha)
    val tertiaryTint = MaterialTheme.colorScheme.tertiary.copy(alpha = tertiaryAlpha)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
            .drawBehind {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(primaryTint, Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.12f),
                        radius = size.width * 0.90f
                    )
                )
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(tertiaryTint, Color.Transparent),
                        center = Offset(size.width * 0.15f, size.height * 0.88f),
                        radius = size.width * 0.80f
                    )
                )
            },
        content = content
    )
}


