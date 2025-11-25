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
        _state.value = _state.value.copy(
            playlists = listOf(Playlist("food", "Еда"), Playlist("medicine", "Медицина"),
                Playlist("dictionaryB2", "Словарь B2"), Playlist("dictionaryC1", "Словарь C1"))
        )
    }
}