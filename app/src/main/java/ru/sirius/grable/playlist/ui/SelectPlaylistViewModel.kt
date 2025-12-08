package ru.sirius.grable.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.sirius.grable.common.AppDatabase
import ru.sirius.grable.playlist.domain.SelectPlaylistRepository
import ru.sirius.grable.playlist.domain.SelectPlaylistRepositoryImpl

data class Playlist(
    val id: Long = 0L,
    val name: String,
    val description: String? = null
)

data class PlaylistState(
    val playlists: List<Playlist> = emptyList()
)

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(PlaylistState())
    val state = _state.asStateFlow()

    private val repository: SelectPlaylistRepository = SelectPlaylistRepositoryImpl(
        AppDatabase.getDatabase(application).playlistDao()
    )

    init {
        viewModelScope.launch {
            repository.getPlaylists().collectLatest { playlists ->
                _state.value = PlaylistState(playlists)
            }
        }
    }

    fun refresh() = viewModelScope.launch {
        repository.getPlaylists().collectLatest { playlists ->
            _state.value = PlaylistState(playlists)
        }
    }
}