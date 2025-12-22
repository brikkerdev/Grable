package ru.sirius.api.domain

import kotlinx.coroutines.flow.Flow
import ru.sirius.api.data.SettingValues

interface ISettingsInteractor {
    fun getSettings(): Flow<Map<String, SettingValues<*>>>
    fun getAvailableLanguages(): Flow<Map<String, String>>
    fun getAvailableVoices(): Flow<Map<String, String>>
    fun getAvailableThemes(): Flow<Map<String, String>>
    fun updateValue(value: SettingValues<*>)
}