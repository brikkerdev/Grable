package ru.sirius.api.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.sirius.api.data.SettingValues.BooleanValue
import ru.sirius.api.data.SettingValues.StringValue

interface ISettingsRepository {
    fun loadSettings() : Map<String, SettingValues<*>>
    fun initSettings()
    fun stringValue(id: String): StringValue
    fun booleanValue(id: String) : BooleanValue
    fun get() : StateFlow<Map<String, SettingValues<*>>>
    fun getAvailableLanguages() : Flow<Map<String, String>>
    fun getAvailableThemes() : Flow<Map<String, String>>
    fun getAvailableVoices() : Flow<Map<String, String>>
    fun update(value: SettingValues<*>);
}