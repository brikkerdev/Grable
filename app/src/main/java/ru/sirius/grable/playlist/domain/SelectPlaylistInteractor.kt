package ru.sirius.grable.main

import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.playlist.domain.SelectPlaylistRepository

class SelectPlaylistInteractor(private val repository: SelectPlaylistRepository) {
    operator fun invoke(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }
}