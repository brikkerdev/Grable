package ru.sirius.grable.add_word.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.sirius.grable.add_word.data.Example
import ru.sirius.grable.add_word.domain.AddWordInteractor
import ru.sirius.grable.add_word.domain.AddWordRepositoryImpl

data class AddWordState(
    val word: String = "",
    val transcription: String = "",
    val translation: String = "",
    val examples: List<Example> = emptyList()
)

class AddWordViewModel : ViewModel() {

    private val repository = AddWordRepositoryImpl()
    private val interactor = AddWordInteractor(repository)

    val state: StateFlow<AddWordState> = combineFlows()

    init {
        viewModelScope.launch {
            interactor.generateFakeExamples(3)
        }
    }

    private fun combineFlows(): StateFlow<AddWordState> {
        return kotlinx.coroutines.flow.combine(
            interactor.word,
            interactor.transcription,
            interactor.translation,
            interactor.examples
        ) { word, transcription, translation, examples ->
            AddWordState(word, transcription, translation, examples)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), AddWordState())
    }

    fun setWord(value: String) {
        viewModelScope.launch { interactor.setWord(value) }
    }

    fun setTranscription(value: String) {
        viewModelScope.launch { interactor.setTranscription(value) }
    }

    fun setTranslation(value: String) {
        viewModelScope.launch { interactor.setTranslation(value) }
    }

    fun addExample(example: Example) {
        viewModelScope.launch { interactor.addExample(example) }
    }

    fun updateExamples(newList: List<Example>) {
        viewModelScope.launch { interactor.updateExamples(newList) }
    }

    fun clear() {
        viewModelScope.launch { interactor.clear() }
    }
}