package ru.sirius.grable.learn.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.sirius.grable.learn.data.LearnPlaylistObject.words
import ru.sirius.grable.learn.ui.Word


class WordsRepository {
    fun getWords(playlistId: Long) : Flow<List<Word>> {
        return flowOf( words.filter{it.playlistId == playlistId})
    }
}