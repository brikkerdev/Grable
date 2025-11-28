package ru.sirius.grable.settings.domain

import ru.sirius.grable.settings.data.Language
import ru.sirius.grable.settings.data.SettingsState
import ru.sirius.grable.settings.data.Theme
import ru.sirius.grable.settings.data.Voice

data class SettingsUIState(
    val settings: SettingsState = SettingsState(),
    val availableLanguages: List<Language> = emptyList(),
    val availableVoices: List<Voice> = emptyList(),
    val availableThemes: List<Theme> = emptyList(),
    val appVersion: String = "",
    val isLoading: Boolean = true
)