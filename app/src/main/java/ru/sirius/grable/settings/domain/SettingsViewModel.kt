package ru.sirius.grable.settings.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.sirius.grable.settings.data.Language
import ru.sirius.grable.settings.data.SettingsRepository
import ru.sirius.grable.settings.data.Theme
import ru.sirius.grable.settings.data.Voice

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor = SettingsInteractor()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        collectSettingsData()
    }

    private fun collectSettingsData() {
        combine(
            settingsInteractor.getSettings(),
            settingsInteractor.getAvailableLanguages(),
            settingsInteractor.getAvailableVoices(),
            settingsInteractor.getAvailableThemes(),
            settingsInteractor.getAppVersion()
        ) { settings, languages, voices, themes, version ->
            SettingsUiState(
                settings = settings,
                availableLanguages = languages,
                availableVoices = voices,
                availableThemes = themes,
                appVersion = version,
                texts = SettingsTexts(
                    mainSettingsTitle = settingsInteractor.getMainSettingsTitle(),
                    audioSettingsTitle = settingsInteractor.getAudioSettingsTitle(),
                    notificationsTitle = settingsInteractor.getNotificationsTitle(),
                    aboutTitle = settingsInteractor.getAboutTitle(),
                    nativeLanguageTitle = settingsInteractor.getNativeLanguageTitle(),
                    themeTitle = settingsInteractor.getThemeTitle(),
                    voiceTitle = settingsInteractor.getVoiceTitle(),
                    remindersTitle = settingsInteractor.getRemindersTitle(),
                    progressNotificationsTitle = settingsInteractor.getProgressNotificationsTitle(),
                    aboutAppTitle = settingsInteractor.getAboutAppTitle(),
                    progressNotificationsSubtitle = settingsInteractor.getProgressNotificationsSubtitle(),
                    aboutAppSubtitle = settingsInteractor.getAboutAppSubtitle(version)
                ),
                isLoading = false
            )
        }.onEach { newState ->
            _uiState.value = newState
        }.launchIn(viewModelScope)
    }

    fun updateNativeLanguage(language: Language) {
        viewModelScope.launch {
            settingsInteractor.updateNativeLanguage(language)
        }
    }

    fun updateVoiceType(voice: Voice) {
        viewModelScope.launch {
            settingsInteractor.updateVoiceType(voice)
        }
    }

    fun updateTheme(theme: Theme) {
        viewModelScope.launch {
            settingsInteractor.updateTheme(theme)
            applyTheme(theme)
        }
    }

    fun toggleDailyReminders(enabled: Boolean) {
        viewModelScope.launch {
            settingsInteractor.toggleDailyReminders(enabled)
        }
    }

    fun updateReminderTime(time: String) {
        viewModelScope.launch {
            settingsInteractor.updateReminderTime(time)
        }
    }

    fun toggleProgressNotifications(enabled: Boolean) {
        viewModelScope.launch {
            settingsInteractor.toggleProgressNotifications(enabled)
        }
    }

    private fun applyTheme(theme: Theme) {
        // Логика применения темы
        // AppCompatDelegate.setDefaultNightMode(...)
    }
}

data class SettingsTexts(
    val mainSettingsTitle: String = "",
    val audioSettingsTitle: String = "",
    val notificationsTitle: String = "",
    val aboutTitle: String = "",
    val nativeLanguageTitle: String = "",
    val themeTitle: String = "",
    val voiceTitle: String = "",
    val remindersTitle: String = "",
    val progressNotificationsTitle: String = "",
    val aboutAppTitle: String = "",
    val progressNotificationsSubtitle: String = "",
    val aboutAppSubtitle: String = ""
)

data class SettingsUiState(
    val settings: ru.sirius.grable.settings.data.SettingsState = ru.sirius.grable.settings.data.SettingsState(),
    val availableLanguages: List<Language> = emptyList(),
    val availableVoices: List<Voice> = emptyList(),
    val availableThemes: List<Theme> = emptyList(),
    val appVersion: String = "",
    val texts: SettingsTexts = SettingsTexts(),
    val isLoading: Boolean = true
)