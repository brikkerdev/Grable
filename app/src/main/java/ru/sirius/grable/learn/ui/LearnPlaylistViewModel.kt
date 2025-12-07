package ru.sirius.grable.learn.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.sirius.grable.learn.domain.LearnPlaylistInteractor
import ru.sirius.grable.learn.domain.WordsRepository

data class Word(
    val id: Long,
    val playlistId: Long,
    val original: String,
    val translation: String,
    val transcription: String,
    val example: String
)

data class WordState(
    val words: List<Word> = emptyList()
)

class LearnPlaylistViewModel: ViewModel() {
    private val _state = MutableStateFlow(WordState())
    val state = _state.asStateFlow()
    private val repository: WordsRepository = WordsRepository()
    private val learnPlaylistInteractor: LearnPlaylistInteractor = LearnPlaylistInteractor(repository)

    init {
        loadWords()
    }

    private fun loadWords(playlistId: Long=1) {
        viewModelScope.launch {
            learnPlaylistInteractor.getWordsById(playlistId).collectLatest { words ->
                _state.value = WordState(words)
            }
        }
    }
}