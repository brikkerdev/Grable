package ru.sirius.grable.feature.playlist.impl.domain

import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.feature.playlist.api.Playlist

class SelectPlaylistInteractor(private val repository: SelectPlaylistRepository) {
    operator fun invoke(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }
}