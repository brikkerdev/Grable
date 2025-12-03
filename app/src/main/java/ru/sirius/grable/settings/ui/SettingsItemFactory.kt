package ru.sirius.grable.settings.ui

import ru.sirius.grable.R
import ru.sirius.grable.settings.domain.SettingsUiState

class SettingsItemsFactory {

    fun createSettingsItems(uiState: SettingsUiState): List<SettingItem> {
        val texts = uiState.texts

        return listOf(
            // Основные настройки
            SettingItem.SectionTitle(
                id = R.id.section_title,
                title = texts.mainSettingsTitle
            ),
            SettingItem.BaseSetting(
                id = R.id.native_language_layout,
                title = texts.nativeLanguageTitle,
                value = uiState.settings.nativeLanguage.name,
                showDivider = true,
            ),
            SettingItem.BaseSetting(
                id = R.id.theme_layout,
                title = texts.themeTitle,
                value = uiState.settings.theme.name,
                showDivider = false,
            ),

            // Аудио настройки
            SettingItem.SectionTitle(
                id = R.id.section_title,
                title = texts.audioSettingsTitle
            ),
            SettingItem.BaseSetting(
                id = R.id.voice_type_layout,
                title = texts.voiceTitle,
                value = uiState.settings.voiceType.name,
                showDivider = true,
            ),

            // Уведомления
            SettingItem.SectionTitle(
                id = R.id.section_title,
                title = texts.notificationsTitle
            ),
            SettingItem.SwitchSetting(
                id = R.id.switchReminders,
                title = texts.remindersTitle,
                subtitle = uiState.settings.reminderTime,
                isChecked = uiState.settings.dailyRemindersEnabled,
                showDivider = true,
            ),
            SettingItem.SwitchSetting(
                id = R.id.switchProgressNotifications,
                title = texts.progressNotificationsTitle,
                subtitle = texts.progressNotificationsSubtitle,
                isChecked = uiState.settings.progressNotificationsEnabled,
                showDivider = false,
            ),

            // О приложении
            SettingItem.SectionTitle(
                id = R.id.section_title,
                title = texts.aboutTitle
            ),
            SettingItem.BaseSetting(
                id = R.id.about_layout,
                title = texts.aboutAppTitle,
                value = "",
                showDivider = true,
            ),

            // Версия приложения
            SettingItem.AppVersion(
                id = R.id.app_version,
                version = texts.aboutAppSubtitle
            )
        )
    }
}