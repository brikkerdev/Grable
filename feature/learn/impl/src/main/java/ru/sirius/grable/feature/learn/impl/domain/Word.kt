package ru.sirius.grable.feature.learn.impl.domain

import ru.sirius.fsrs.FlashCard
import java.io.Serializable

data class Word(
    val id: Long,
    val playlistId: Long,
    val original: String,
    val translation: String,
    val transcription: String?,
    val examples: List<WordExample>,
    val imageUrl: String?,
    val fsrsData: FlashCard
) : Serializable