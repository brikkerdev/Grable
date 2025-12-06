package ru.sirius.grable.settings.domain

import ru.sirius.grable.settings.data.SettingValues

data class SettingsUIState(
    val values: Map<String, SettingValues<*>> = emptyMap(),
    val availableLanguages: Map<String, String> = emptyMap(),
    val availableVoices: Map<String, String> = emptyMap(),
    val availableThemes: Map<String, String> = emptyMap(),
)