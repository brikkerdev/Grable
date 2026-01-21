package ru.sirius.grable.feature.learn.impl.domain

import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.feature.playlist.api.Playlist

class LearnPlaylistInteractor(
    private val repository: WordsRepository
) {
    fun getWordsById(playlistId: Long): Flow<List<Word>> {
        return repository.getWords(playlistId)
    }

    fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    suspend fun addWord(
        playlistId: Long,
        original: String,
        transcription: String,
        translation: String,
        exampleText: String?,
        exampleTranslation: String?
    ) {
        repository.addWord(
            playlistId = playlistId,
            original = original,
            transcription = transcription,
            translation = translation,
            exampleText = exampleText,
            exampleTranslation = exampleTranslation
        )
    }

    suspend fun deleteWord(wordId: Long) {
        repository.deleteWord(wordId)
    }
}

