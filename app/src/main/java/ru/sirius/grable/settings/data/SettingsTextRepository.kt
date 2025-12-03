package ru.sirius.grable.settings.data

class SettingsTextsRepository {
    fun getMainSettingsTitle(): String = "Основные настройки"
    fun getAudioSettingsTitle(): String = "Аудио настройки"
    fun getNotificationsTitle(): String = "Уведомления"
    fun getAboutTitle(): String = "О приложении"

    fun getNativeLanguageTitle(): String = "Родной язык"
    fun getThemeTitle(): String = "Цветовая тема"
    fun getVoiceTitle(): String = "Озвучивание диктора"
    fun getRemindersTitle(): String = "Напоминания"
    fun getProgressNotificationsTitle(): String = "Уведомления о прогрессе"
    fun getAboutAppTitle(): String = "О приложении"

    fun getProgressNotificationsSubtitle(): String = "Еженедельные отчеты о прогрессе"
    fun getAboutAppSubtitle(appVersion: String): String = "Версия $appVersion"
}