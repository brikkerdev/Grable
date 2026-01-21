package ru.sirius.grable.feature.playlist.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.sirius.grable.feature.playlist.api.PlaylistState
import ru.sirius.grable.feature.playlist.impl.domain.CreatePlaylistInteractor
import ru.sirius.grable.feature.playlist.impl.domain.SelectPlaylistInteractor
import ru.sirius.grable.feature.settings.api.ISettingsService


class SelectPlaylistViewModel() : ViewModel(), KoinComponent {

    private val interactor by inject<SelectPlaylistInteractor>()
    private val createPlaylistInteractor by inject<CreatePlaylistInteractor>()
    private val settingsService: ISettingsService by inject()
    private val _state = MutableStateFlow(PlaylistState())
    val state = _state.asStateFlow()

    init {
        val savedIds = settingsService.getSelectedPlaylistIds().toSet()
        _state.value = _state.value.copy(selectedPlaylistIds = savedIds)
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            interactor.invoke().collectLatest { playlists ->
                _state.value = _state.value.copy(
                    playlists = playlists,
                    isLoading = false
                )
            }
        }
    }

    fun refresh() = viewModelScope.launch {
        _state.value = _state.value.copy(isLoading = true)
        interactor.invoke().collectLatest { playlists ->
            _state.value = _state.value.copy(
                playlists = playlists,
                isLoading = false
            )
        }
    }

    fun createPlaylist(name: String, description: String?) {
        viewModelScope.launch {
            createPlaylistInteractor.createUserPlaylist(name, description)
        }
    }

    fun toggleEditMode() {
        _state.value = _state.value.copy(
            isEditMode = !_state.value.isEditMode
        )
    }

    fun togglePlaylistSelection(playlistId: Long) {
        val currentSelected = _state.value.selectedPlaylistIds.toMutableSet()
        if (currentSelected.contains(playlistId)) {
            currentSelected.remove(playlistId)
        } else {
            currentSelected.add(playlistId)
        }
        _state.value = _state.value.copy(selectedPlaylistIds = currentSelected)
    }

    fun saveSelectedPlaylists() {
        val selectedIds = _state.value.selectedPlaylistIds.toList()
        settingsService.saveSelectedPlaylistIds(selectedIds)
        _state.value = _state.value.copy(isEditMode = false)
    }

    fun applySelectedPlaylists() {
        saveSelectedPlaylists()
    }
}