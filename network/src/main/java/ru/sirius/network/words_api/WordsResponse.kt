package ru.sirius.network.words_api

data class WordsResponse(
    val record: RecordDto
)

data class RecordDto(
    val playlists: List<PlaylistDto> = emptyList()
)

data class PlaylistDto(
    val name: String,
    val words: List<WordDto> = emptyList()
)

data class WordDto(
    val original: String,
    val translation: String,
    val transcription: String? = ""
)

