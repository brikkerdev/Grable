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
import ru.sirius.grable.feature.learn.api.Word
import ru.sirius.grable.feature.learn.impl.domain.LearnPlaylistInteractor

data class WordState(
    val words: List<Word> = emptyList(),
    val isLoading: Boolean = true
)

class LearnPlaylistViewModel(
    private val playlistId: Long
) : ViewModel(), KoinComponent {
    
    private val interactor: LearnPlaylistInteractor by inject()
    
    private val _state = MutableStateFlow(WordState())
    val state = _state.asStateFlow()

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            interactor.getWordsById(playlistId)
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

