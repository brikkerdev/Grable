package ru.sirius.grable.feature.add_word.impl.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.sirius.grable.feature.add_word.api.data.Example
import ru.sirius.grable.feature.add_word.api.domain.IAddWordRepository
import ru.sirius.grable.feature.add_word.impl.data.AddExampleObject
import java.util.Random

class AddWordRepositoryImpl : IAddWordRepository {

    private val _word = MutableStateFlow("")
    override val word: Flow<String> = _word

    private val _transcription = MutableStateFlow("")
    override val transcription: Flow<String> = _transcription

    private val _translation = MutableStateFlow("")
    override val translation: Flow<String> = _translation

    private val _examples = MutableStateFlow<List<Example>>(emptyList())
    override val examples: Flow<List<Example>> = _examples

    override suspend fun setWord(value: String) {
        _word.value = value
    }

    override suspend fun setTranscription(value: String) {
        _transcription.value = value
    }

    override suspend fun setTranslation(value: String) {
        _translation.value = value
    }

    override suspend fun addExample(example: Example) {
        if (example.english.isNotBlank()) {
            _examples.value = _examples.value + example
        }
    }

    override suspend fun updateExamples(newList: List<Example>) {
        _examples.value = newList
    }

    override suspend fun generateFakeExamples(count: Int) {
        val samples = AddExampleObject.samples

        val newList = _examples.value.toMutableList()
        val random = Random()
        repeat(count) {
            newList.add(samples[random.nextInt(samples.size)])
        }
        _examples.value = newList
    }

    override suspend fun clear() {
        _word.value = ""
        _transcription.value = ""
        _translation.value = ""
        _examples.value = emptyList()
    }
}

