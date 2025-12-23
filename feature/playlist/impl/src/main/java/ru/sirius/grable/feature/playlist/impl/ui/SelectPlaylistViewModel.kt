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
import ru.sirius.grable.feature.playlist.impl.domain.SelectPlaylistInteractor


class SelectPlaylistViewModel() : ViewModel(), KoinComponent {

    private val interactor by inject<SelectPlaylistInteractor>()
    private val _state = MutableStateFlow(PlaylistState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            interactor.invoke().collectLatest { playlists ->
                val loading = playlists.isEmpty()
                _state.value = PlaylistState(playlists = playlists, isLoading = loading)
            }
        }
    }

    fun refresh() = viewModelScope.launch {
        _state.value = _state.value.copy(isLoading = true)
        interactor.invoke().collectLatest { playlists ->
            val loading = playlists.isEmpty()
            _state.value = PlaylistState(playlists = playlists, isLoading = loading)
        }
    }
}