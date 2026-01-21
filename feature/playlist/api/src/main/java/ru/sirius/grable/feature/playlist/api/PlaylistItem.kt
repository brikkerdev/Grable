package ru.sirius.grable.feature.playlist.api

sealed class PlaylistListItem {
    data class PlaylistItem(val playlist: Playlist) : PlaylistListItem()
    object Separator : PlaylistListItem()
}

