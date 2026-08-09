package com.example.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class ThemeMode {
    SYSTEM, DARK, LIGHT
}

enum class ColorSchemeSource {
    STATIC, DYNAMIC
}

/**
 * High-contrast, energetic 'Vibrant Palette' using Material3 colorScheme.
 * Features Neon Purple as Primary, Electric Blue as Secondary, and Vivid Lime for Tertiary/Action states.
 */
val VibrantPalette = darkColorScheme(
  primary = Color(0xFFD0BCFF),
  onPrimary = Color(0xFF380066),
  primaryContainer = Color(0xFF4F378B),
  onPrimaryContainer = Color(0xFFEADDFF),
  secondary = Color(0xFF70D2FF),
  onSecondary = Color(0xFF003547),
  secondaryContainer = Color(0xFF004D65),
  onSecondaryContainer = Color(0xFFC2E8FF),
  tertiary = Color(0xFFB8F349),
  onTertiary = Color(0xFF263500),
  tertiaryContainer = Color(0xFF394D00),
  onTertiaryContainer = Color(0xFFD4FF70),
  background = Color(0xFF0D0B14),
  onBackground = Color(0xFFE6E1E9),
  surface = Color(0xFF181524),
  onSurface = Color(0xFFE6E1E9),
  surfaceVariant = Color(0xFF262135),
  onSurfaceVariant = Color(0xFFCAC4D0),
  outline = Color(0xFF938F99),
  outlineVariant = Color(0xFF49454F),
  error = CooldownRed,
  onError = Color.White
)

val LightVibrantPalette = lightColorScheme(
  primary = Color(0xFF6750A4),
  onPrimary = Color.White,
  primaryContainer = Color(0xFFEADDFF),
  onPrimaryContainer = Color(0xFF21005D),
  secondary = Color(0xFF006875),
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFADECFF),
  onSecondaryContainer = Color(0xFF001F26),
  tertiary = Color(0xFF4C6600),
  onTertiary = Color.White,
  tertiaryContainer = Color(0xFFC6F16D),
  onTertiaryContainer = Color(0xFF141F00),
  background = Color(0xFFFEF7FF),
  onBackground = Color(0xFF1D1B20),
  surface = Color(0xFFFAF7FC),
  onSurface = Color(0xFF1D1B20),
  surfaceVariant = Color(0xFFE7E0EC),
  onSurfaceVariant = Color(0xFF49454F),
  outline = Color(0xFF79747E),
  outlineVariant = Color(0xFFCAC4D0),
  error = CooldownRed,
  onError = Color.White
)

@Composable
fun ColorScheme.animateColors(durationMillis: Int = 400): ColorScheme {
    val spec = tween<Color>(durationMillis = durationMillis)
    return copy(
        primary = animateColorAsState(primary, spec, label = "primary").value,
        onPrimary = animateColorAsState(onPrimary, spec, label = "onPrimary").value,
        primaryContainer = animateColorAsState(primaryContainer, spec, label = "primaryContainer").value,
        onPrimaryContainer = animateColorAsState(onPrimaryContainer, spec, label = "onPrimaryContainer").value,
        inversePrimary = animateColorAsState(inversePrimary, spec, label = "inversePrimary").value,
        secondary = animateColorAsState(secondary, spec, label = "secondary").value,
        onSecondary = animateColorAsState(onSecondary, spec, label = "onSecondary").value,
        secondaryContainer = animateColorAsState(secondaryContainer, spec, label = "secondaryContainer").value,
        onSecondaryContainer = animateColorAsState(onSecondaryContainer, spec, label = "onSecondaryContainer").value,
        tertiary = animateColorAsState(tertiary, spec, label = "tertiary").value,
        onTertiary = animateColorAsState(onTertiary, spec, label = "onTertiary").value,
        tertiaryContainer = animateColorAsState(tertiaryContainer, spec, label = "tertiaryContainer").value,
        onTertiaryContainer = animateColorAsState(onTertiaryContainer, spec, label = "onTertiaryContainer").value,
        background = animateColorAsState(background, spec, label = "background").value,
        onBackground = animateColorAsState(onBackground, spec, label = "onBackground").value,
        surface = animateColorAsState(surface, spec, label = "surface").value,
        onSurface = animateColorAsState(onSurface, spec, label = "onSurface").value,
        surfaceVariant = animateColorAsState(surfaceVariant, spec, label = "surfaceVariant").value,
        onSurfaceVariant = animateColorAsState(onSurfaceVariant, spec, label = "onSurfaceVariant").value,
        surfaceTint = animateColorAsState(surfaceTint, spec, label = "surfaceTint").value,
        inverseSurface = animateColorAsState(inverseSurface, spec, label = "inverseSurface").value,
        inverseOnSurface = animateColorAsState(inverseOnSurface, spec, label = "inverseOnSurface").value,
        error = animateColorAsState(error, spec, label = "error").value,
        onError = animateColorAsState(onError, spec, label = "onError").value,
        errorContainer = animateColorAsState(errorContainer, spec, label = "errorContainer").value,
        onErrorContainer = animateColorAsState(onErrorContainer, spec, label = "onErrorContainer").value,
        outline = animateColorAsState(outline, spec, label = "outline").value,
        outlineVariant = animateColorAsState(outlineVariant, spec, label = "outlineVariant").value,
        scrim = animateColorAsState(scrim, spec, label = "scrim").value
    )
}

@Composable
fun CrackheadTheme(
  themeMode: ThemeMode = ThemeMode.SYSTEM,
  colorSchemeSource: ColorSchemeSource = ColorSchemeSource.STATIC,
  content: @Composable () -> Unit,
) {
  val darkTheme = when (themeMode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.DARK -> true
    ThemeMode.LIGHT -> false
  }

  val context = LocalContext.current
  val targetColors = when {
    colorSchemeSource == ColorSchemeSource.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> VibrantPalette
    else -> LightVibrantPalette
  }

  val animatedColors = targetColors.animateColors(durationMillis = 400)

  MaterialTheme(
    colorScheme = animatedColors,
    typography = Typography,
    content = content
  )
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  CrackheadTheme(
    themeMode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT,
    colorSchemeSource = if (dynamicColor) ColorSchemeSource.DYNAMIC else ColorSchemeSource.STATIC,
    content = content
  )
}



