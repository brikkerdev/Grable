package ru.sirius.grable.learn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.sirius.grable.learn.data.WordsRepository
import ru.sirius.grable.main.Playlist

data class Word(
    val id: Long,
    val playlistId: Long,
    val original: String,
    val translation: String,
    val transcription: String,
    val example: String
)

class LearnPlaylistViewModel(
    private val repository: WordsRepository
): ViewModel() {
    private val _words = MutableLiveData<List<Word>>()
    val words: LiveData<List<Word>> = _words

    init {
        loadWords()
    }

    private fun loadWords(playlistId: Long=1) {
        _words.value = repository.getWordsByPlaylistId(playlistId)
    }
}