package ru.sirius.grable.learn.domain

import android.provider.UserDictionary
import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.learn.ui.Word

class LearnPlaylistInteractor(
    private val repository: WordsRepository
) {
    fun getWordsById(playlistId: Long): Flow<List<Word>> {
        return repository.getWords(playlistId)
    }
}