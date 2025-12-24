package ru.sirius.grable.feature.playlist.api

data class Playlist(
    val id: Long = 0L,
    val name: String,
    val description: String? = null
)
