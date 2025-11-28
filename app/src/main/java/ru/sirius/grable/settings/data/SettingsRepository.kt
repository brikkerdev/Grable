package ru.sirius.grable.settings.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

class SettingsRepository {
    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    private val availableLanguages = listOf(
        Language("ru", "Русский"),
        Language("en", "English"),
        Language("es", "Español"),
        Language("fr", "Français"),
        Language("de", "Deutsch"),
    )

    private val availableVoices = listOf(
        Voice("female", "Женский"),
        Voice("male", "Мужской"),
    )

    private val availableThemes = listOf(
        Theme("system", "Системная"),
        Theme("light", "Светлая"),
        Theme("dark", "Тёмная"),
    )

    private val appVersion = "1.0.0"

    // Flow методы
    fun get(): Flow<SettingsState> = _settingsState

    fun getAvailableLanguages(): Flow<List<Language>> = flowOf(availableLanguages)

    fun getAvailableVoices(): Flow<List<Voice>> = flowOf(availableVoices)

    fun getAvailableThemes(): Flow<List<Theme>> = flowOf(availableThemes)

    fun getAppVersion(): Flow<String> = flowOf(appVersion)

    // Методы обновления
    suspend fun updateNativeLanguage(language: Language) {
        _settingsState.value = _settingsState.value.copy(nativeLanguage = language)
    }

    suspend fun updateVoiceType(voice: Voice) {
        _settingsState.value = _settingsState.value.copy(voiceType = voice)
    }

    suspend fun updateTheme(theme: Theme) {
        _settingsState.value = _settingsState.value.copy(theme = theme)
    }

    suspend fun toggleDailyReminders(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(dailyRemindersEnabled = enabled)
    }

    suspend fun updateReminderTime(time: String) {
        _settingsState.value = _settingsState.value.copy(reminderTime = time)
    }

    suspend fun toggleProgressNotifications(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(progressNotificationsEnabled = enabled)
    }
}