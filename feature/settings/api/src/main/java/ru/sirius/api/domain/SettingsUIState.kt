package ru.sirius.api.domain

import ru.sirius.api.data.SettingValues

data class SettingsUIState(
    val values: Map<String, SettingValues<*>> = emptyMap(),
    val availableLanguages: Map<String, String> = emptyMap(),
    val availableVoices: Map<String, String> = emptyMap(),
    val availableThemes: Map<String, String> = emptyMap(),
)