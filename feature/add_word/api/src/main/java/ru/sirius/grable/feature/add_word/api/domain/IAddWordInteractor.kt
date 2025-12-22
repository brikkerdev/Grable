package ru.sirius.grable.feature.add_word.api.domain

import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.feature.add_word.api.data.Example

interface IAddWordInteractor {
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

