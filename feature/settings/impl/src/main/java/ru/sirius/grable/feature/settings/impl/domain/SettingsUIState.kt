package ru.sirius.grable.feature.settings.impl.domain

import ru.sirius.grable.feature.settings.impl.data.SettingValues

data class SettingsUIState(
    val values: Map<String, SettingValues<*>> = emptyMap(),
    val availableLanguages: Map<String, String> = emptyMap(),
    val availableVoices: Map<String, String> = emptyMap(),
    val availableThemes: Map<String, String> = emptyMap(),
)