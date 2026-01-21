package ru.sirius.grable.feature.learn.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.sirius.grable.feature.learn.impl.domain.LearnInteractor
import ru.sirius.grable.feature.learn.impl.domain.LearnPlaylistInteractor
import ru.sirius.grable.feature.learn.impl.domain.Word
import ru.sirius.grable.feature.playlist.api.Playlist

data class WordState(
    val words: List<Word> = emptyList(),
    val isLoading: Boolean = true
)

class LearnPlaylistViewModel(
    private val playlistId: Long
) : ViewModel(), KoinComponent {
    
    private val playlistInteractor: LearnPlaylistInteractor by inject()
    private val learnInteractor: LearnInteractor by inject()
    
    private val _state = MutableStateFlow(WordState())
    val state = _state.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists = _playlists.asStateFlow()

    init {
        loadWords()
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists()
                .collectLatest { playlists ->
                    _playlists.value = playlists
                }
        }
    }

    private fun loadWords() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            playlistInteractor.getWordsById(playlistId)
                .catch {
                    _state.value = _state.value.copy(words = emptyList(), isLoading = false)
                }
                .collectLatest { words ->
                    _state.value = WordState(words = words, isLoading = false)
                }
        }
    }

    fun markAsKnown(word: Word) {
        viewModelScope.launch {
            learnInteractor.markAsKnown(word)
            loadWords()
        }
    }

    fun addWord(
        playlistId: Long,
        original: String,
        translation: String,
        transcription: String,
        example: String?,
        exampleTranslation: String?
    ) {
        viewModelScope.launch {
            playlistInteractor.addWord(
                playlistId = playlistId,
                original = original,
                transcription = transcription,
                translation = translation,
                exampleText = example,
                exampleTranslation = exampleTranslation
            )
        }
    }

    fun deleteWord(wordId: Long) {
        viewModelScope.launch {
            playlistInteractor.deleteWord(wordId)
            loadWords()
        }
    }
}

