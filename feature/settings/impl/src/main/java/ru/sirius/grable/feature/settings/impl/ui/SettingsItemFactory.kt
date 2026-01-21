package ru.sirius.grable.feature.settings.impl.ui


import ru.sirius.grable.feature.settings.impl.domain.SettingsUIState
import ru.sirius.feature.settings.impl.R
import ru.sirius.grable.feature.settings.api.SettingsKeys

class SettingsItemsFactory {

    fun createSettingsItems(
        uiState: SettingsUIState,
        languageValue: String,
        voiceValue: String,
        themeValue: String,
    ): List<SettingItem> {

        val values = uiState.values

        return listOf(
            // Основные настройки
            SettingItem.SectionTitle(
                id = R.id.section_title,
                title = R.string.settings_general
            ),
            SettingItem.BaseSetting(
                id = R.id.native_language_layout,
                title = R.string.native_language,
                value =  languageValue,
            ),
            SettingItem.BaseSetting(
                id = R.id.theme_layout,
                title = R.string.theme_layout,
                value = themeValue,
            ),
            // Аудио настройки
            SettingItem.SectionTitle(
                id = R.id.section_title,
                title = R.string.settings_audio,
            ),
            SettingItem.BaseSetting(
                id = R.id.voice_type_layout,
                title = R.string.voice_type_layout,
                value = voiceValue,
            ),
            SettingItem.SectionTitle(
                id = R.id.section_learning,
                title = R.string.settings_learning
            ),
            SettingItem.BaseSetting(
                id = R.id.new_words_per_day_layout,
                title = R.string.settings_new_words_per_day,
                value = values[SettingsKeys.NEW_WORDS_PER_DAY]?.intValue()?.toString() ?: "10",
            ),
            SettingItem.SectionTitle(
                id = R.id.section_notifications,
                title = R.string.settings_notifications
            ),
            SettingItem.SwitchSetting(
                id = R.id.switchReminders,
                title = R.string.settings_daily_notifications,
                subtitle = values[SettingsKeys.TIME_REMINDER]?.stringValue().orEmpty(),
                isChecked = values[SettingsKeys.DAILY_REMINDER]?.booleanValue() ?: false,
            ),
            // О приложении
            SettingItem.SectionTitle(
                id = R.id.section_title,
                title = R.string.settings_about
            ),
            SettingItem.BaseSetting(
                id = R.id.about_layout,
                title = R.string.settings_about,
                value = "",
            ),
            // Версия приложения
            SettingItem.AppVersion(
                id = R.id.app_version,
                version = values[SettingsKeys.APP_VERSION]?.stringValue().orEmpty()
            )
        )
    }
}