package ru.sirius.grable.feature.learn.impl.domain

import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.feature.learn.api.Word

class LearnPlaylistInteractor(
    private val repository: WordsRepository
) {
    fun getWordsById(playlistId: Long): Flow<List<Word>> {
        return repository.getWords(playlistId)
    }
}

