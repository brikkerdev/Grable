package ru.sirius.grable.feature.playlist.impl.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sirius.grable.core.database.PlaylistDao
import ru.sirius.grable.core.database.PlaylistEntity

interface CreatePlaylistRepository {
    suspend fun createUserPlaylist(name: String, description: String?): Long
}

class CreatePlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao
) : CreatePlaylistRepository {
    override suspend fun createUserPlaylist(name: String, description: String?): Long = withContext(Dispatchers.IO) {
        val existing = playlistDao.findByName(name)
        if (existing != null) return@withContext existing.id

        playlistDao.insert(
            PlaylistEntity(
                id = 0,
                name = name,
                description = description,
                isSystem = false
            )
        )
    }
}


