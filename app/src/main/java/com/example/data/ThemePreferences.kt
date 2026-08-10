package com.example.data

import android.content.Context
import com.example.ui.theme.ColorSchemeSource
import com.example.ui.theme.ThemeMode

class ThemePreferences(context: Context) {
    private val prefs = context.getSharedPreferences("crackhead_theme_preferences", Context.MODE_PRIVATE)

    var themeMode: ThemeMode
        get() {
            val name = prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
            return try {
                ThemeMode.valueOf(name)
            } catch (e: Exception) {
                ThemeMode.SYSTEM
            }
        }
        set(value) {
            prefs.edit().putString("theme_mode", value.name).apply()
        }

    var colorSchemeSource: ColorSchemeSource
        get() {
            val name = prefs.getString("color_scheme_source", ColorSchemeSource.DYNAMIC.name) ?: ColorSchemeSource.DYNAMIC.name
            return try {
                ColorSchemeSource.valueOf(name)
            } catch (e: Exception) {
                ColorSchemeSource.DYNAMIC
            }
        }
        set(value) {
            prefs.edit().putString("color_scheme_source", value.name).apply()
        }

    var defaultGraceMinutes: Int
        get() = prefs.getInt("default_grace_minutes", 1)
        set(value) {
            prefs.edit().putInt("default_grace_minutes", value).apply()
        }
}
