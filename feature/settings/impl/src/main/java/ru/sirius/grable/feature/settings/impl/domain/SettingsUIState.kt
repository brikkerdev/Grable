package ru.sirius.grable.feature.settings.impl.domain

import androidx.appcompat.app.AppCompatDelegate
import ru.sirius.grable.feature.settings.impl.data.SettingValues

data class SettingsUIState(
    val values: Map<String, SettingValues<*>> = emptyMap(),
    val availableLanguageIds: List<String> = listOf("ru", "en"),
    val availableVoiceIds: List<String> = listOf("male", "female"),
    val availableThemeIds: List<String> = listOf(
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString(),
        AppCompatDelegate.MODE_NIGHT_NO.toString(),
        AppCompatDelegate.MODE_NIGHT_YES.toString()
    ),
)