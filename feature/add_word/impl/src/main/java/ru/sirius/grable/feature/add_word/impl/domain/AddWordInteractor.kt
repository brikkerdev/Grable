package ru.sirius.grable.feature.add_word.impl.domain

import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.feature.add_word.api.data.Example
import ru.sirius.grable.feature.add_word.api.domain.IAddWordRepository
import ru.sirius.grable.feature.playlist.api.Playlist

class AddWordInteractor(
    private val repository: AddWordRepository
) {
    fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    suspend fun saveWord(
        playlistId: Long,
        original: String,
        transcription: String,
        translation: String,
        examples: List<Example>
    ): Long {
        return repository.saveWord(playlistId, original, transcription, translation, examples)
    }
}
