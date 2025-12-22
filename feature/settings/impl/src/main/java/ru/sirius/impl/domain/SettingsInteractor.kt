package ru.sirius.impl.domain

import kotlinx.coroutines.flow.Flow
import ru.sirius.api.data.SettingValues
import ru.sirius.api.data.ISettingsRepository
import ru.sirius.api.domain.ISettingsInteractor

class SettingsInteractor(
    private val settingsRepository: ISettingsRepository,
) : ISettingsInteractor {

    override fun getSettings(): Flow<Map<String, SettingValues<*>>> = settingsRepository.get()

    override fun getAvailableLanguages() = settingsRepository.getAvailableLanguages()

    override fun getAvailableVoices() = settingsRepository.getAvailableVoices()
    override fun getAvailableThemes() = settingsRepository.getAvailableThemes()

    override fun updateValue(value: SettingValues<*>) {
        settingsRepository.update(value)
    }
}