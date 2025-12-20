package ru.sirius.grable.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.sirius.grable.settings.data.SettingValues
import ru.sirius.grable.settings.data.SettingsRepository
import ru.sirius.grable.settings.domain.SettingsInteractor
import ru.sirius.grable.settings.domain.SettingsUIState

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUIState())
    val uiState: StateFlow<SettingsUIState> = _uiState.asStateFlow()

    init {
        collectSettingsData()
    }

    private fun collectSettingsData() {
        combine(
            settingsInteractor.getSettings(),
            settingsInteractor.getAvailableLanguages(),
            settingsInteractor.getAvailableVoices(),
            settingsInteractor.getAvailableThemes(),
        ) { values, languages, voices, themes ->
            SettingsUIState(
                values = values,
                availableLanguages = languages,
                availableVoices = voices,
                availableThemes = themes,
            )
        }.onEach { newState ->
            _uiState.value = newState
        }.launchIn(viewModelScope)
    }

    fun update(value: SettingValues<*>) {
        viewModelScope.launch {
            settingsInteractor.updateValue(value)
        }
    }

    private fun applyTheme() {
        // Логика применения темы
        // AppCompatDelegate.setDefaultNightMode(...)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY])

                return SettingsViewModel(
                    SettingsInteractor(SettingsRepository(application))
                ) as T
            }
        }
    }
}