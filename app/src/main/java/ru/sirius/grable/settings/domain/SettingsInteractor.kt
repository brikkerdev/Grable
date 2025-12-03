package ru.sirius.grable.settings.domain

import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.settings.data.Language
import ru.sirius.grable.settings.data.SettingsRepository
import ru.sirius.grable.settings.data.SettingsState
import ru.sirius.grable.settings.data.SettingsTextsRepository
import ru.sirius.grable.settings.data.Theme
import ru.sirius.grable.settings.data.Voice

class SettingsInteractor(
    private val settingsRepository: SettingsRepository = SettingsRepository(),
    private val textsRepository: SettingsTextsRepository = SettingsTextsRepository()
) {

    fun getSettings(): Flow<SettingsState> = settingsRepository.get()
    fun getAvailableLanguages(): Flow<List<Language>> = settingsRepository.getAvailableLanguages()
    fun getAvailableVoices(): Flow<List<Voice>> = settingsRepository.getAvailableVoices()
    fun getAvailableThemes(): Flow<List<Theme>> = settingsRepository.getAvailableThemes()
    fun getAppVersion(): Flow<String> = settingsRepository.getAppVersion()

    // Методы для получения текстов
    fun getMainSettingsTitle(): String = textsRepository.getMainSettingsTitle()
    fun getAudioSettingsTitle(): String = textsRepository.getAudioSettingsTitle()
    fun getNotificationsTitle(): String = textsRepository.getNotificationsTitle()
    fun getAboutTitle(): String = textsRepository.getAboutTitle()

    fun getNativeLanguageTitle(): String = textsRepository.getNativeLanguageTitle()
    fun getThemeTitle(): String = textsRepository.getThemeTitle()
    fun getVoiceTitle(): String = textsRepository.getVoiceTitle()
    fun getRemindersTitle(): String = textsRepository.getRemindersTitle()
    fun getProgressNotificationsTitle(): String = textsRepository.getProgressNotificationsTitle()
    fun getAboutAppTitle(): String = textsRepository.getAboutAppTitle()

    fun getProgressNotificationsSubtitle(): String = textsRepository.getProgressNotificationsSubtitle()
    fun getAboutAppSubtitle(appVersion: String): String = textsRepository.getAboutAppSubtitle(appVersion)

    suspend fun updateNativeLanguage(language: Language) {
        settingsRepository.updateNativeLanguage(language)
    }

    suspend fun updateVoiceType(voice: Voice) {
        settingsRepository.updateVoiceType(voice)
    }

    suspend fun updateTheme(theme: Theme) {
        settingsRepository.updateTheme(theme)
    }

    suspend fun toggleDailyReminders(enabled: Boolean) {
        settingsRepository.toggleDailyReminders(enabled)
    }

    suspend fun updateReminderTime(time: String) {
        settingsRepository.updateReminderTime(time)
    }

    suspend fun toggleProgressNotifications(enabled: Boolean) {
        settingsRepository.toggleProgressNotifications(enabled)
    }
}