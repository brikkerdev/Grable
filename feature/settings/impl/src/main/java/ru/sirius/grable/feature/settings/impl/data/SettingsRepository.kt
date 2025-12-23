package ru.sirius.grable.feature.settings.impl.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.getString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import ru.sirius.feature.settings.impl.R
import ru.sirius.grable.feature.settings.impl.data.SettingValues.StringValue
import ru.sirius.grable.feature.settings.impl.data.SettingValues.BooleanValue

const val ID_LANGUAGE = "nativeLanguageId"
const val ID_VOICE = "voiceId"
const val ID_THEME = "themeId"
const val ID_DAILY_REMINDER = "toggleDailyReminders"
const val ID_TIME_REMINDER = "reminderTime"
const val ID_NOTIFICATION_PROGRESS = "progressNotifications"
const val ID_APP_VERSION = "appVersion"

class SettingsRepository (
    private val context: Context
) {
    private val settingsProvider = SettingsProvider(context)
    private val _settingsState by lazy { MutableStateFlow(loadSettings()) }

    init {
        initSettings()
    }

    fun loadSettings() : Map<String, SettingValues<*>> {
        return mapOf(
            ID_LANGUAGE to stringValue(ID_LANGUAGE),
            ID_VOICE to stringValue(ID_VOICE),
            ID_THEME to stringValue(ID_THEME),
            ID_DAILY_REMINDER to booleanValue(ID_DAILY_REMINDER),
            ID_TIME_REMINDER to stringValue(ID_TIME_REMINDER),
            ID_NOTIFICATION_PROGRESS to booleanValue(ID_NOTIFICATION_PROGRESS),
            ID_APP_VERSION to stringValue(ID_APP_VERSION)
        )
    }

    fun initSettings() {
        if (settingsProvider.getStringValue(ID_LANGUAGE) == "NO DATA") {
            settingsProvider.setStringValue(ID_LANGUAGE, "ru")
        }

        if (settingsProvider.getStringValue(ID_VOICE) == "NO DATA") {
            settingsProvider.setStringValue(ID_VOICE, "female")
        }

        if (settingsProvider.getStringValue(ID_THEME) == "NO DATA") {
            settingsProvider.setStringValue(ID_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString())
        }

        if (settingsProvider.getStringValue(ID_TIME_REMINDER) == "NO DATA") {
            settingsProvider.setStringValue(ID_TIME_REMINDER, "20:00")
        }

        if (settingsProvider.getStringValue(ID_APP_VERSION) == "NO DATA") {
            settingsProvider.setStringValue(ID_APP_VERSION, "1.0.0")
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

    fun get() = _settingsState.asStateFlow()

    fun getAvailableLanguages() : Flow<Map<String, String>> {
        return flowOf(mapOf (
            "ru" to context.getString(R.string.caption_lang_ru),
            "en" to context.getString(R.string.caption_lang_en)
        ))
    }

    fun getAvailableThemes() : Flow<Map<String, String>> {
        return flowOf(mapOf(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString() to context.getString(R.string.settings_theme_system),
            AppCompatDelegate.MODE_NIGHT_NO.toString() to context.getString(R.string.settings_theme_light),
            AppCompatDelegate.MODE_NIGHT_YES.toString() to context.getString(R.string.settings_theme_dark)
        ))
    }

    fun getAvailableVoices() : Flow<Map<String, String>> {
        return flowOf(mapOf(
            "male" to context.getString(R.string.settings_voice_male),
            "female" to context.getString(R.string.settings_voice_female)
        ))
    }

    fun update(value: SettingValues<*>) {
        when(value) {
            is BooleanValue -> settingsProvider.setBoolValue(value.id, value.value)
            is StringValue -> settingsProvider.setStringValue(value.id, value.value)
        }

        _settingsState.value += value.id to value
    }
}