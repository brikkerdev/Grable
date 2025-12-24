package ru.sirius.grable.feature.learn.api

import java.io.Serializable

data class Word(
    val id: Long,
    val playlistId: Long,
    val original: String,
    val translation: String,
    val transcription: String,
    val example: String
) : Serializable

