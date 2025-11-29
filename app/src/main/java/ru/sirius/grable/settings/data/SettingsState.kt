package ru.sirius.grable.settings.data

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