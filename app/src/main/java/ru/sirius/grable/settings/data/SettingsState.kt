package ru.sirius.grable.settings.data

import ru.sirius.grable.settings.data.SettingValues.BooleanValue
import ru.sirius.grable.settings.data.SettingValues.StringValue

data class SettingsState(
    val nativeLanguage: StringValue,
    val voiceType: StringValue,
    val theme: StringValue,
    val dailyRemindersEnabled: BooleanValue,
    val reminderTime: StringValue,
    val progressNotificationsEnabled: BooleanValue,
    val appVersion: StringValue,
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