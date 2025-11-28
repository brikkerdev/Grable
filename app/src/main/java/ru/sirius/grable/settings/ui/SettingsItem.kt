package ru.sirius.grable.settings.ui

sealed class SettingItem {
    data class SectionTitle(val title: String) : SettingItem()

    data class BaseSetting(
        val id: Int,
        val title: String,
        val value: String,
        val showDivider: Boolean = false,
        val onClick: (() -> Unit)? = null
    ) : SettingItem()

    data class SwitchSetting(
        val id: Int,
        val title: String,
        val subtitle: String,
        val isChecked: Boolean,
        val showDivider: Boolean = false,
        val onCheckedChange: ((Boolean) -> Unit)? = null
    ) : SettingItem()

    data class AppVersion(val version: String) : SettingItem()
}