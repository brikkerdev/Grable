package ru.sirius.grable.add_word.domain

import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.add_word.ui.Example

interface AddWordRepository {
    val word: Flow<String>
    suspend fun setWord(value: String)

    val transcription: Flow<String>
    suspend fun setTranscription(value: String)

    val translation: Flow<String>
    suspend fun setTranslation(value: String)

    val examples: Flow<List<Example>>
    suspend fun addExample(example: Example)
    suspend fun updateExamples(newList: List<Example>)
    suspend fun generateFakeExamples(count: Int)
    suspend fun clear()
}