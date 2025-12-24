package ru.sirius.grable.feature.add_word.impl.ui

import ru.sirius.grable.feature.add_word.api.data.Example

data class AddWordState(
    val word: String = "",
    val transcription: String = "",
    val translation: String = "",
    val examples: List<Example> = emptyList()
)

