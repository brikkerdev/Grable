package ru.sirius.grable.add_word.domain

import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.add_word.ui.Example

class AddWordInteractor(private val repository: AddWordRepository) {

    val word: Flow<String> = repository.word
    suspend fun setWord(value: String) = repository.setWord(value)

    val transcription: Flow<String> = repository.transcription
    suspend fun setTranscription(value: String) = repository.setTranscription(value)

    val translation: Flow<String> = repository.translation
    suspend fun setTranslation(value: String) = repository.setTranslation(value)

    val examples: Flow<List<Example>> = repository.examples
    suspend fun addExample(example: Example) = repository.addExample(example)
    suspend fun updateExamples(newList: List<Example>) = repository.updateExamples(newList)
    suspend fun generateFakeExamples(count: Int) = repository.generateFakeExamples(count)
    suspend fun clear() = repository.clear()
}