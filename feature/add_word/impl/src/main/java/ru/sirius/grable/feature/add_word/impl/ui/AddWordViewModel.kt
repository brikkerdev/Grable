package ru.sirius.grable.feature.add_word.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.sirius.grable.feature.add_word.api.data.Example
import ru.sirius.grable.feature.add_word.impl.domain.AddWordInteractor
import ru.sirius.grable.feature.playlist.api.Playlist

class AddWordViewModel(
    private val interactor: AddWordInteractor
) : ViewModel() {

    private val _state = MutableStateFlow(AddWordState())
    val state: StateFlow<AddWordState> = _state.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists = _playlists.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            interactor.getAllPlaylists()
                .catch { _playlists.value = emptyList() }
                .collectLatest { _playlists.value = it }
        }
    }

    fun setWord(value: String) {
        _state.value = _state.value.copy(word = value)
    }

    fun setTranscription(value: String) {
        _state.value = _state.value.copy(transcription = value)
    }

    fun setTranslation(value: String) {
        _state.value = _state.value.copy(translation = value)
    }

    fun addExample(example: Example) {
        val currentExamples = _state.value.examples.toMutableList()
        currentExamples.add(example)
        _state.value = _state.value.copy(examples = currentExamples)
    }

    fun removeExample(example: Example) {
        val currentExamples = _state.value.examples.toMutableList()
        currentExamples.remove(example)
        _state.value = _state.value.copy(examples = currentExamples)
    }

    fun updateExample(oldExample: Example, newExample: Example) {
        val currentExamples = _state.value.examples.toMutableList()
        val index = currentExamples.indexOfFirst { it.id == oldExample.id }
        if (index >= 0) {
            currentExamples[index] = newExample
            _state.value = _state.value.copy(examples = currentExamples)
        }
    }

    suspend fun saveWord(
        playlistId: Long,
        original: String,
        transcription: String,
        translation: String,
        examples: List<Example>
    ): Boolean {
        if (original.isBlank() || translation.isBlank()) {
            return false
        }

        _isSaving.value = true
        return try {
            interactor.saveWord(
                playlistId = playlistId,
                original = original.trim(),
                transcription = transcription.trim(),
                translation = translation.trim(),
                examples = examples
            )
            _saveSuccess.value = true
            clear()
            true
        } catch (e: Exception) {
            false
        } finally {
            _isSaving.value = false
        }
    }

    fun clear() {
        _state.value = AddWordState()
        _saveSuccess.value = false
    }
}

