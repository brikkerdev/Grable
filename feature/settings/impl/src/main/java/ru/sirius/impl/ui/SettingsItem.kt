package ru.sirius.impl.ui

sealed interface SettingItem {
    val id : Int

    data class SectionTitle(
        override val id: Int,
        val title: Int
    ) : SettingItem

    data class BaseSetting(
        override val id: Int,
        val title: Int,
        val value: String,
        val showDivider: Boolean = false,
    ) : SettingItem

    data class SwitchSetting(
        override val id: Int,
        val title: Int,
        val subtitle: String,
        val isChecked: Boolean,
        val showDivider: Boolean = false,
    ) : SettingItem

    data class AppVersion(
        override val id: Int,
        val version: String
    ) : SettingItem
}