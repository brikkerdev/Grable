package ru.sirius.grable.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Playlist(
    val id: String,
    val name: String,
    val description: String? = null
)

data class PlaylistState(
    val playlists: List<Playlist>
)

class PlaylistViewModel : ViewModel() {
    private val _state = MutableStateFlow(PlaylistState(listOf()))
    val state = _state.asStateFlow()

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            _state.value = PlaylistState(
                listOf(
                    Playlist("food", "Еда", "Слова о еде"),
                    Playlist("medicine", "Медицина", "Медицинские термины"),
                    Playlist("animals", "Животные", "Названия животных"),
                    Playlist("transport", "Транспорт", "Виды транспорта и связанные слова"),
                    Playlist("technology", "Технологии", "Технические термины"),
                    Playlist("clothes", "Одежда", "Предметы одежды и аксессуары"),
                    Playlist("nature", "Природа", "Слова о природных явлениях и объектах"),
                    Playlist("house", "Дом", "Предметы и понятия, связанные с домом"),
                    Playlist("school", "Школа", "Учебные принадлежности и термины"),
                    Playlist("sports", "Спорт", "Виды спорта и спортивные термины"),
                    Playlist("jobs", "Профессии", "Названия профессий")
                )
            )
        }
    }

    fun refreshPlaylists() {
        loadPlaylists()
    }
}