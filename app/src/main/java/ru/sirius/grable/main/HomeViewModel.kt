package ru.sirius.grable.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Playlist (
    val id: String,
    val name: String
)

data class State (
    val playlists: List<Playlist>
)

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(State(listOf()))
    val state = _state.asStateFlow()

    fun getPlaylists() {
//        return _state.value.playlists;
    }
}