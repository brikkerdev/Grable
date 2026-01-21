package ru.sirius.grable.feature.settings.api

interface ISettingsService {
    fun getNewWordsPerDay(): Int
    fun getDailyReminder(): Boolean
    fun getTimeReminder(): String
    fun getLanguage(): String
    fun getTheme(): String
    fun getSelectedPlaylistIds(): List<Long>
    fun saveSelectedPlaylistIds(ids: List<Long>)
}

