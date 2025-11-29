package ru.sirius.grable.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class Playlist(
    val id: String,
    val name: String,
    val description: String? = null
)

data class PlaylistState(
    val playlists: List<Playlist> = emptyList()
)

class PlaylistViewModel : ViewModel() {
    private val _state = MutableStateFlow(PlaylistState())
    val state = _state.asStateFlow()

    private val repository: PlaylistRepository = PlaylistRepositoryImpl()
    private val selectPlaylistInteractor: SelectPlaylistInteractor = SelectPlaylistInteractor(repository)

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            selectPlaylistInteractor.invoke().collectLatest { playlists ->
                _state.value = PlaylistState(playlists)
            }
        }
    }

    fun refreshPlaylists() {
        loadPlaylists()
    }
}