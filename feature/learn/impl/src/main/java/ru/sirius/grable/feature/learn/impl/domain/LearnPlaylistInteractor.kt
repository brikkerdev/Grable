package ru.sirius.grable.feature.learn.impl.domain

import kotlinx.coroutines.flow.Flow

class LearnPlaylistInteractor(
    private val repository: WordsRepository
) {
    fun getWordsById(playlistId: Long): Flow<List<Word>> {
        return repository.getWords(playlistId)
    }
}

