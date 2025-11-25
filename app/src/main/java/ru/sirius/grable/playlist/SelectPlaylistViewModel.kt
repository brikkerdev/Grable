package ru.sirius.grable.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Playlist(
    val name: String,
    val description: String
)

class SelectPlaylistViewModel : ViewModel() {
    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        _playlists.value = listOf(
            Playlist("Еда", "Много слов про еду"),
            Playlist("Музыка", "Много слов про музыку"),
            Playlist("Словарь B1", "Слова уровня B1"),
            Playlist("Словарь C1", "Слова уровня C1"),
            Playlist("Путешествия", "Фразы для аэропорта и отеля"),
            Playlist("IT термины", "Английский для программистов")
        )
    }
}