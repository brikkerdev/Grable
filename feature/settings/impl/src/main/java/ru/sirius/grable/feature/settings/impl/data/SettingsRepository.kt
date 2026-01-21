package ru.sirius.grable.feature.settings.impl.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.getString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import ru.sirius.feature.settings.impl.R
import ru.sirius.grable.feature.settings.api.ISettingsService
import ru.sirius.grable.feature.settings.api.SettingsKeys
import ru.sirius.grable.feature.settings.impl.data.SettingValues.StringValue
import ru.sirius.grable.feature.settings.impl.data.SettingValues.BooleanValue

class SettingsRepository (
    private val context: Context
) : ISettingsService {
    private val settingsProvider = SettingsProvider(context)
    private val _settingsState by lazy { MutableStateFlow(loadSettings()) }

    init {
        initSettings()
    }

    fun loadSettings() : Map<String, SettingValues<*>> {
        return mapOf(
            SettingsKeys.LANGUAGE to stringValue(SettingsKeys.LANGUAGE),
            SettingsKeys.VOICE to stringValue(SettingsKeys.VOICE),
            SettingsKeys.THEME to stringValue(SettingsKeys.THEME),
            SettingsKeys.DAILY_REMINDER to booleanValue(SettingsKeys.DAILY_REMINDER),
            SettingsKeys.TIME_REMINDER to stringValue(SettingsKeys.TIME_REMINDER),
            SettingsKeys.NOTIFICATION_PROGRESS to booleanValue(SettingsKeys.NOTIFICATION_PROGRESS),
            SettingsKeys.APP_VERSION to stringValue(SettingsKeys.APP_VERSION),
            SettingsKeys.NEW_WORDS_PER_DAY to intValue(SettingsKeys.NEW_WORDS_PER_DAY)
        )
    }

    fun initSettings() {
        if (settingsProvider.getStringValue(SettingsKeys.LANGUAGE) == "NO DATA") {
            settingsProvider.setStringValue(SettingsKeys.LANGUAGE, "ru")
        }

        if (settingsProvider.getStringValue(SettingsKeys.VOICE) == "NO DATA") {
            settingsProvider.setStringValue(SettingsKeys.VOICE, "female")
        }

        if (settingsProvider.getStringValue(SettingsKeys.THEME) == "NO DATA") {
            settingsProvider.setStringValue(SettingsKeys.THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString())
        }

        if (settingsProvider.getStringValue(SettingsKeys.TIME_REMINDER) == "NO DATA") {
            settingsProvider.setStringValue(SettingsKeys.TIME_REMINDER, "20:00")
        }

        if (settingsProvider.getStringValue(SettingsKeys.APP_VERSION) == "NO DATA") {
            settingsProvider.setStringValue(SettingsKeys.APP_VERSION, "1.0.0")
        }
    }

    fun stringValue(id: String): StringValue {
        return StringValue(
            id = id,
            value = settingsProvider.getStringValue(id)
        )
    }

    fun booleanValue(id: String) : BooleanValue {
        return BooleanValue(
            id = id,
            value = settingsProvider.getBoolValue(id)
        )
    }

    fun intValue(id: String) : SettingValues.IntValue {
        return SettingValues.IntValue(
            id = id,
            value = settingsProvider.getIntValue(id, 10)
        )
    }

    override fun getNewWordsPerDay(): Int {
        return settingsProvider.getIntValue(SettingsKeys.NEW_WORDS_PER_DAY, 10)
    }

    override fun getDailyReminder(): Boolean {
        return settingsProvider.getBoolValue(SettingsKeys.DAILY_REMINDER)
    }

    override fun getTimeReminder(): String {
        return settingsProvider.getStringValue(SettingsKeys.TIME_REMINDER)
    }

    override fun getLanguage(): String {
        return settingsProvider.getStringValue(SettingsKeys.LANGUAGE)
    }

    override fun getTheme(): String {
        return settingsProvider.getStringValue(SettingsKeys.THEME)
    }

    override fun getSelectedPlaylistIds(): List<Long> {
        return settingsProvider.getLongListValue(SettingsKeys.SELECTED_PLAYLIST_IDS)
    }

    override fun saveSelectedPlaylistIds(ids: List<Long>) {
        settingsProvider.setLongListValue(SettingsKeys.SELECTED_PLAYLIST_IDS, ids)
    }

    fun get() = _settingsState.asStateFlow()

    fun getAvailableLanguages(): Flow<List<String>> = flowOf(listOf("ru", "en"))

    fun getAvailableVoices(): Flow<List<String>> = flowOf(listOf("male", "female"))

    fun getAvailableThemes(): Flow<List<String>> = flowOf(
        listOf(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString(),
            AppCompatDelegate.MODE_NIGHT_NO.toString(),
            AppCompatDelegate.MODE_NIGHT_YES.toString()
        )
    )

    fun update(value: SettingValues<*>) {
        when(value) {
            is BooleanValue -> settingsProvider.setBoolValue(value.id, value.value)
            is StringValue -> settingsProvider.setStringValue(value.id, value.value)
            is SettingValues.IntValue -> settingsProvider.setIntValue(value.id, value.value)
        }

        _settingsState.value += value.id to value
    }
}