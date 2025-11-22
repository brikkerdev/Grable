package ru.sirius.grable.add_word

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddWordViewModel : ViewModel() {

    val word = MutableStateFlow("")
    val transcription = MutableStateFlow("")
    val translation = MutableStateFlow("")

    private val _examples = MutableStateFlow<List<String>>(emptyList())
    val examples: StateFlow<List<String>> = _examples

    fun addExample(text: String) {
        if (text.isNotBlank()) {
            _examples.value = _examples.value + text
        }
    }

    fun updateExamples(newList: List<String>) {
        _examples.value = newList
    }

    fun generateFakeExamples(count: Int) {
        val samples = listOf(
            "This word is used very often. — Это слово используется очень часто.",
            "Here is an example sentence. — Вот пример предложения.",
            "Native speakers use it every day. — Носители языка используют это каждый день.",
            "Sometimes it has another meaning. — Иногда оно имеет другое значение.",
            "It is common in spoken English. — Это часто встречается в разговорном английском."
        )

        val newList = _examples.value.toMutableList()
        repeat(count) {
            newList.add(samples.random())
        }
        _examples.value = newList
    }

    fun clear() {
        word.value = ""
        transcription.value = ""
        translation.value = ""
        _examples.value = emptyList()
    }
}
