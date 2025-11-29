package ru.sirius.grable.main

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.sirius.grable.playlist.data.SelectPlaylistObject

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
}

class PlaylistRepositoryImpl : PlaylistRepository {
    override fun getPlaylists(): Flow<List<Playlist>> {
        return flowOf(SelectPlaylistObject.playlists)
    }
}