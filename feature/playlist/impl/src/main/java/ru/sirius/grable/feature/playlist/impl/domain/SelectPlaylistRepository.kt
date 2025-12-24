package ru.sirius.grable.feature.playlist.impl.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sirius.grable.core.database.PlaylistDao
import ru.sirius.grable.core.database.PlaylistEntity
import ru.sirius.grable.feature.playlist.api.Playlist

interface SelectPlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
}

class SelectPlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao
) : SelectPlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists()
            .map { entities: List<PlaylistEntity> ->
                entities.map { entity ->
                    Playlist(
                        id = entity.id,
                        name = entity.name,
                        description = entity.description
                    )
                }
            }
    }
}