package ru.sirius.grable.feature.learn.impl.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sirius.grable.core.database.AppDatabase
import ru.sirius.grable.core.database.WordDao
import ru.sirius.grable.feature.learn.api.Word

class WordsRepository(
    private val wordDao: WordDao
) {
    fun getWords(playlistId: Long): Flow<List<Word>> {
        return wordDao.observeWords(playlistId).map { entities ->
            entities.map { entity ->
                Word(
                    id = entity.id,
                    playlistId = entity.playlistId,
                    original = entity.original,
                    translation = entity.translation,
                    transcription = entity.transcription,
                    example = "" // Примеры можно получить из ExampleEntity при необходимости
                )
            }
        }
    }
}

