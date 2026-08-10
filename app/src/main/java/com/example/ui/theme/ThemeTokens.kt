package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Centralized Material 3 Semantic Theme Tokens.
 *
 * All UI components should derive their colors from these semantic tokens,
 * ensuring proper M3 surface-container tonal hierarchy, text legibility,
 * and semantic state alignment across both Light and Dark themes.
 */
object ThemeTokens {

    // =========================================================================
    // 1. Surface & Container Tonal Hierarchy
    // =========================================================================

    /** Base application screen background. */
    val AppBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.background

    /** Standard surface for elevated cards or dialogs. */
    val Surface: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surface

    /** Lowest emphasis container (e.g., subtle inset or background card). */
    val ContainerLowest: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surfaceContainerLowest

    /** Low emphasis container (e.g., statistics cards, background panels). */
    val ContainerLow: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surfaceContainerLow

    /** Standard container (e.g., settings cards, list items). */
    val Container: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surfaceContainer

    /** High emphasis container (e.g., active/highlighted cards). */
    val ContainerHigh: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surfaceContainerHigh

    /** Highest emphasis container (e.g., navigation rails, floating headers). */
    val ContainerHighest: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surfaceContainerHighest

    /** Border color for cards and elevated surfaces. */
    val CardBorder: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.outlineVariant

    // =========================================================================
    // 2. Primary / Accent Semantic Roles
    // =========================================================================

    val Primary: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.primary

    val OnPrimary: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onPrimary

    val PrimaryContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.primaryContainer

    val OnPrimaryContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onPrimaryContainer

    val SecondaryContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.secondaryContainer

    val OnSecondaryContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onSecondaryContainer

    // =========================================================================
    // 3. Text & Content Legibility Hierarchy
    // =========================================================================

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

    // =========================================================================
    // 4. Centralized Semantic State Tokens (Success, Error, Warning, Info)
    // =========================================================================

    // --- SUCCESS / ENABLED / ACTIVE ---
    val Success: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.tertiary

    val SuccessContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.tertiaryContainer

    val OnSuccessContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onTertiaryContainer

    val SuccessBorder: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)

    // --- ERROR / DESTRUCTIVE / BLOCKED ---
    val Error: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.error

    val ErrorContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.errorContainer

    val OnErrorContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onErrorContainer

    // --- WARNING / ATTENTION ---
    val Warning: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.secondary

    val WarningContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.secondaryContainer

    val OnWarningContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onSecondaryContainer

    // --- INFORMATIONAL ---
    val Info: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.primary

    val InfoContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.primaryContainer

    val OnInfoContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onPrimaryContainer
}
