package ru.sirius.grable.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    // Состояние настроек
    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    // Доступные языки
    val availableLanguages = listOf(
        Language("ru", "Русский"),
        Language("en", "English"),
        Language("es", "Español"),
        Language("fr", "Français"),
        Language("de", "Deutsch")
    )

    // Доступные голоса
    val availableVoices = listOf(
        Voice("female", "Женский"),
        Voice("male", "Мужской")
    )

    // Доступные темы
    val availableThemes = listOf(
        Theme("system", "Системная"),
        Theme("light", "Светлая"),
        Theme("dark", "Тёмная")
    )

    // Доступное время для напоминаний
    val availableReminderTimes = listOf(
        "08:00", "09:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00", "17:00",
        "18:00", "19:00", "20:00", "21:00"
    )

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // Здесь должна быть загрузка из SharedPreferences/DataStore
            val currentSettings = SettingsState(
                nativeLanguage = availableLanguages.first(),
                voiceType = availableVoices.first(),
                theme = availableThemes.first(),
                dailyRemindersEnabled = true,
                reminderTime = "19:00",
                progressNotificationsEnabled = true,
                appVersion = "1.0.0"
            )
            _settingsState.value = currentSettings
        }
    }

    fun updateNativeLanguage(language: Language) {
        viewModelScope.launch {
            _settingsState.value = _settingsState.value.copy(nativeLanguage = language)
            saveSettings()
        }
    }

    fun updateVoiceType(voice: Voice) {
        viewModelScope.launch {
            _settingsState.value = _settingsState.value.copy(voiceType = voice)
            saveSettings()
        }
    }

    fun updateTheme(theme: Theme) {
        viewModelScope.launch {
            _settingsState.value = _settingsState.value.copy(theme = theme)
            saveSettings()
        }
    }

    fun toggleDailyReminders(enabled: Boolean) {
        viewModelScope.launch {
            _settingsState.value = _settingsState.value.copy(dailyRemindersEnabled = enabled)
            saveSettings()
        }
    }

    fun updateReminderTime(time: String) {
        viewModelScope.launch {
            _settingsState.value = _settingsState.value.copy(reminderTime = time)
            saveSettings()
        }
    }

    fun toggleProgressNotifications(enabled: Boolean) {
        viewModelScope.launch {
            _settingsState.value = _settingsState.value.copy(progressNotificationsEnabled = enabled)
            saveSettings()
        }
    }

    private fun saveSettings() {
        viewModelScope.launch {
            val state = _settingsState.value
            // Реализация сохранения в SharedPreferences/DataStore
            // Здесь можно добавить применение темы
            applyTheme(state.theme)
        }
    }

    private fun applyTheme(theme: Theme) {
        // Здесь будет логика применения темы
        // Например, через AppCompatDelegate.setDefaultNightMode()
    }

    fun getAppVersion(): String {
        return _settingsState.value.appVersion
    }
}

data class SettingsState(
    val nativeLanguage: Language = Language("ru", "Русский"),
    val voiceType: Voice = Voice("female", "Женский"),
    val theme: Theme = Theme("system", "Системная"),
    val dailyRemindersEnabled: Boolean = true,
    val reminderTime: String = "19:00",
    val progressNotificationsEnabled: Boolean = true,
    val appVersion: String = "1.0.0"
)

data class Language(
    val code: String,
    val name: String
)

data class Voice(
    val id: String,
    val name: String
)

data class Theme(
    val id: String,
    val name: String
)