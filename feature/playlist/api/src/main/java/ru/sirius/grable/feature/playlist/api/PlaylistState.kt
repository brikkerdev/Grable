package ru.sirius.grable.feature.playlist.api

data class PlaylistState(
    val playlists: List<Playlist> = emptyList(),
    val isLoading: Boolean = true
)
