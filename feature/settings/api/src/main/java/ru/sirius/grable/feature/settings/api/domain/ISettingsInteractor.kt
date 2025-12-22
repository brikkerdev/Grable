package ru.sirius.grable.feature.settings.api.domain

import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.feature.settings.api.data.SettingValues

interface ISettingsInteractor {
    fun getSettings(): Flow<Map<String, SettingValues<*>>>
    fun getAvailableLanguages(): Flow<Map<String, String>>
    fun getAvailableVoices(): Flow<Map<String, String>>
    fun getAvailableThemes(): Flow<Map<String, String>>
    fun updateValue(value: SettingValues<*>)
}