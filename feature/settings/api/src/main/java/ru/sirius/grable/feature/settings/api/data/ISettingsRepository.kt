package ru.sirius.grable.feature.settings.api.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ISettingsRepository {
    fun loadSettings() : Map<String, SettingValues<*>>
    fun initSettings()
    fun stringValue(id: String): SettingValues.StringValue
    fun booleanValue(id: String) : SettingValues.BooleanValue
    fun get() : StateFlow<Map<String, SettingValues<*>>>
    fun getAvailableLanguages() : Flow<Map<String, String>>
    fun getAvailableThemes() : Flow<Map<String, String>>
    fun getAvailableVoices() : Flow<Map<String, String>>
    fun update(value: SettingValues<*>);
}