package ru.sirius.grable.feature.settings.impl.data

data class SettingsState(
    val nativeLanguage: SettingValues.StringValue,
    val voiceType: SettingValues.StringValue,
    val theme: SettingValues.StringValue,
    val dailyRemindersEnabled: SettingValues.BooleanValue,
    val reminderTime: SettingValues.StringValue,
    val progressNotificationsEnabled: SettingValues.BooleanValue,
    val appVersion: SettingValues.StringValue,
)

sealed interface SettingValues<T> {
    val id: String
    val value: T

    fun stringValue(): String? {
        return value.toString()
    }

    fun booleanValue() : Boolean? {
        return value.toString().toBoolean()
    }

    data class StringValue(
        override val id: String,
        override val value: String
    ) : SettingValues<String>

    data class BooleanValue(
        override val id: String,
        override val value: Boolean
    ) : SettingValues<Boolean>
}