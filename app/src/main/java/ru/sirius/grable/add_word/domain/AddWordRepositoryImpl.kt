package ru.sirius.grable.add_word.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.sirius.grable.add_word.ui.Example
import ru.sirius.grable.common.ExampleDao
import ru.sirius.grable.common.ExampleEntity

class AddWordRepositoryImpl(private val exampleDao: ExampleDao) : AddWordRepository {

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


        val newList = _examples.value.toMutableList()

        val entities: List<ExampleEntity> = exampleDao.getRandomExamples(count)
        entities.forEach { entity ->
               newList.add(
                    Example(
                        id = entity.id.toInt(), // Assuming IDs fit in Int; adjust if needed
                        english = entity.text,
                        russian = entity.translatedText
         )
   )
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