package ru.sirius.grable.add_word.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.sirius.grable.add_word.domain.AddWordInteractor
import ru.sirius.grable.add_word.domain.AddWordRepositoryImpl
import ru.sirius.grable.common.AppDatabase
import ru.sirius.grable.common.ExampleEntity
import ru.sirius.grable.common.PlaylistEntity
import ru.sirius.grable.common.WordEntity

data class AddWordState(
    val word: String = "",
    val transcription: String = "",
    val translation: String = "",
    val examples: List<Example> = emptyList()
)

class AddWordViewModel(private val database: AppDatabase) : ViewModel() {
    private val repository = AddWordRepositoryImpl(database.exampleDao())
    private val interactor = AddWordInteractor(repository)

    val state: StateFlow<AddWordState> = combineFlows()

    private fun combineFlows(): StateFlow<AddWordState> {
        return combine(
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

    fun save() {
        viewModelScope.launch {
            val currentState = state.value
            if (currentState.word.isBlank()) return@launch

            val playlistDao = database.playlistDao()
            var playlist = playlistDao.getByName("Пользовательское")
            var playlistId = playlist?.id ?: 0L

            if (playlist == null) {
                playlist = PlaylistEntity(0, "Пользовательское", "Слова, добавленные пользователем")
                playlistId = playlistDao.insert(playlist)
            }

            val word = WordEntity(
                id = 0,
                playlistId = playlistId,
                original = currentState.word,
                transcription = currentState.transcription,
                translation = currentState.translation,
                text = "",
                isNew = true
            )

            val wordId = database.wordDao().insertWord(word)

            currentState.examples.forEach { ex ->
                val exampleEntity = ExampleEntity(
                    id = 0,
                    wordId = wordId,
                    text = ex.english,
                    translatedText = ex.russian
                )
                database.exampleDao().insertExample(exampleEntity)
            }

            clear()
        }
    }
}