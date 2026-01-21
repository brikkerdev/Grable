package ru.sirius.grable.feature.playlist.impl.domain

class CreatePlaylistInteractor(
    private val repository: CreatePlaylistRepository
) {
    suspend fun createUserPlaylist(name: String, description: String?) {
        repository.createUserPlaylist(name, description)
    }
}


