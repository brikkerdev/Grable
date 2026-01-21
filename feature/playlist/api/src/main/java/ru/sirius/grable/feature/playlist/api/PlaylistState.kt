package ru.sirius.grable.feature.playlist.api

data class PlaylistState(
    val playlists: List<PlaylistListItem> = emptyList(),
    val isLoading: Boolean = true,
    val isEditMode: Boolean = false,
    val selectedPlaylistIds: Set<Long> = emptySet()
)
