package ru.sirius.grable.settings.ui

sealed interface SettingItem {
    val id : Int

    data class SectionTitle(
        override val id: Int,
        val title: String
    ) : SettingItem

    data class BaseSetting(
        override val id: Int,
        val title: String,
        val value: String,
        val showDivider: Boolean = false,
        val onClick: (() -> Unit)? = null
    ) : SettingItem

    data class SwitchSetting(
        override val id: Int,
        val title: String,
        val subtitle: String,
        val isChecked: Boolean,
        val showDivider: Boolean = false,
        val onCheckedChange: ((Boolean) -> Unit)? = null
    ) : SettingItem

    data class AppVersion(
        override val id: Int,
        val version: String
    ) : SettingItem
}