package ru.sirius.grable.learn.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.sirius.grable.common.AppDatabase
import ru.sirius.grable.learn.domain.LearnPlaylistInteractor
import ru.sirius.grable.learn.domain.WordsRepository
import ru.sirius.network.words_api.NetworkModule

import java.io.Serializable

data class Word(
    val id: Long,
    val playlistId: Long,
    val original: String,
    val translation: String,
    val transcription: String,
    val example: String
) : Serializable

data class WordState(
    val words: List<Word> = emptyList(),
    val isLoading: Boolean = true
)

class LearnPlaylistViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(WordState())
    val state = _state.asStateFlow()
    private val repository: WordsRepository
    private val learnPlaylistInteractor: LearnPlaylistInteractor

    init {
        val db = AppDatabase.getDatabase(application)
        val api = NetworkModule.createWordsApiService()
        repository = WordsRepository(db, api)
        learnPlaylistInteractor = LearnPlaylistInteractor(repository)
        loadWords()
    }

    private fun loadWords(playlistId: Long = 1) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            learnPlaylistInteractor.getWordsById(playlistId)
                .catch {
                    _state.value = _state.value.copy(words = emptyList(), isLoading = true)
                }
                .collectLatest { words ->
                    val loading = words.isEmpty()
                    _state.value = WordState(words = words, isLoading = loading)
                }
        }
    }
}