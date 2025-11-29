package ru.sirius.grable.main

import kotlinx.coroutines.flow.Flow

class SelectPlaylistInteractor(private val repository: PlaylistRepository) {
    operator fun invoke(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }
}