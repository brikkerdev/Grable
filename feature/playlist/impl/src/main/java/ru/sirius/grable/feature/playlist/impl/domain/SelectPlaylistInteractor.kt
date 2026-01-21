package ru.sirius.grable.feature.playlist.impl.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sirius.grable.feature.playlist.api.Playlist
import ru.sirius.grable.feature.playlist.api.PlaylistListItem

class SelectPlaylistInteractor(private val repository: SelectPlaylistRepository) {
    operator fun invoke(): Flow<List<PlaylistListItem>> {
        return repository.getPlaylists()
            .map { playlists ->
                val systemPlaylists = playlists.filter { it.isSystem }.sortedBy { it.name }
                val userPlaylists = playlists.filter { !it.isSystem }.sortedBy { it.name }
                
                val items = mutableListOf<PlaylistListItem>()
                if (systemPlaylists.isNotEmpty()) {
                    items.addAll(systemPlaylists.map { playlist -> PlaylistListItem.PlaylistItem(playlist = playlist) })
                }
                if (userPlaylists.isNotEmpty() && systemPlaylists.isNotEmpty()) {
                    items.add(PlaylistListItem.Separator)
                }
                if (userPlaylists.isNotEmpty()) {
                    items.addAll(userPlaylists.map { playlist -> PlaylistListItem.PlaylistItem(playlist = playlist) })
                }
                items
            }
    }
}