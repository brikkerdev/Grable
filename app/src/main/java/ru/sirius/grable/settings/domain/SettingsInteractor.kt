package ru.sirius.grable.settings.domain

import android.content.Context
import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.settings.data.SettingValues
import ru.sirius.grable.settings.data.SettingsRepository
import ru.sirius.grable.settings.data.SettingsState

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