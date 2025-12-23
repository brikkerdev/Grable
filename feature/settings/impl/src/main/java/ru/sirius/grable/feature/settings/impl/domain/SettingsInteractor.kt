package ru.sirius.grable.feature.settings.impl.domain

import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.feature.settings.impl.data.SettingValues
import ru.sirius.grable.feature.settings.impl.data.SettingsRepository

class SettingsInteractor(
    private val settingsRepository: SettingsRepository,
) {

    fun getSettings(): Flow<Map<String, SettingValues<*>>> = settingsRepository.get()

    fun getAvailableLanguages() = settingsRepository.getAvailableLanguages()

    fun getAvailableVoices() = settingsRepository.getAvailableVoices()
    fun getAvailableThemes() = settingsRepository.getAvailableThemes()

    fun updateValue(value: SettingValues<*>) {
        settingsRepository.update(value)
    }
}