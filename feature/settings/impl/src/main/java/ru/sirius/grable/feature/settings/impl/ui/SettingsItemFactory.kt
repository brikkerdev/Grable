package ru.sirius.grable.feature.settings.impl.ui

import ru.sirius.grable.feature.settings.impl.data.ID_APP_VERSION
import ru.sirius.grable.feature.settings.impl.data.ID_DAILY_REMINDER
import ru.sirius.grable.feature.settings.impl.data.ID_LANGUAGE
import ru.sirius.grable.feature.settings.impl.data.ID_THEME
import ru.sirius.grable.feature.settings.impl.data.ID_TIME_REMINDER
import ru.sirius.grable.feature.settings.impl.data.ID_VOICE
import ru.sirius.grable.feature.settings.api.domain.SettingsUIState
import ru.sirius.feature.settings.impl.R
import kotlin.collections.get

class SettingsItemsFactory {

    fun createSettingsItems(uiState: SettingsUIState): List<SettingItem> {
        val values = uiState.values
        val langs = uiState.availableLanguages
        val voices = uiState.availableVoices
        val themes = uiState.availableThemes

        return listOf(
            // Основные настройки
            SettingItem.SectionTitle(
                id = R.id.section_title,
                title = R.string.settings_general
            ),
            SettingItem.BaseSetting(
                id = R.id.native_language_layout,
                title = R.string.native_language,
                value =  langs[values[ID_LANGUAGE]?.stringValue()].orEmpty(),
                showDivider = true,
            ),
            SettingItem.BaseSetting(
                id = R.id.theme_layout,
                title = R.string.theme_layout,
                value = themes[values[ID_THEME]?.stringValue()].orEmpty(),
                showDivider = false,
            ),

            // Аудио настройки
            SettingItem.SectionTitle(
                id = R.id.section_title,
                title = R.string.settings_audio,
            ),
            SettingItem.BaseSetting(
                id = R.id.voice_type_layout,
                title = R.string.voice_type_layout,
                value = voices[values[ID_VOICE]?.stringValue()].orEmpty(),
                showDivider = true,
            ),

            // Уведомления
            SettingItem.SectionTitle(
                id = R.id.section_title,
                title = R.string.settings_notifications
            ),
            SettingItem.SwitchSetting(
                id = R.id.switchReminders,
                title = R.string.settings_daily_notifications,
                subtitle = values[ID_TIME_REMINDER]?.stringValue().orEmpty(),
                isChecked = values[ID_DAILY_REMINDER]?.booleanValue() ?: false,
                showDivider = true,
            ),
//            SettingItem.SwitchSetting(
//                id = R.id.switchProgressNotifications,
//                title = "Отчет о прогрессе",
//                subtitle = "Еженедельный",
//                isChecked = values[ID_NOTIFICATION_PROGRESS]?.booleanValue() ?: false,
//                showDivider = false,
//            ),

            // О приложении
            SettingItem.SectionTitle(
                id = R.id.section_title,
                title = R.string.settings_about
            ),
            SettingItem.BaseSetting(
                id = R.id.about_layout,
                title = R.string.settings_about,
                value = "",
                showDivider = true,
            ),

            // Версия приложения
            SettingItem.AppVersion(
                id = R.id.app_version,
                version = values[ID_APP_VERSION]?.stringValue().orEmpty()
            )
        )
    }
}