package ru.sirius.grable.learn.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sirius.grable.common.AppDatabase
import ru.sirius.grable.common.PlaylistEntity
import ru.sirius.grable.common.WordEntity
import ru.sirius.grable.learn.ui.Word
import ru.sirius.network.words_api.WordsApiService

class WordsRepository(
    private val db: AppDatabase,
    private val api: WordsApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getWords(playlistId: Long): Flow<List<Word>> {
        val dao = db.wordDao()
        return dao.observeWords(playlistId)
            .onStart {
                val existing = dao.observeWords(playlistId).first()
                if (existing.isEmpty()) {
                    fetchAndCache()
                }
            }
            .mapEntities()
    }

    private fun Flow<List<WordEntity>>.mapEntities(): Flow<List<Word>> =
        map { entities ->
            entities.map { entity ->
                Word(
                    id = entity.id,
                    playlistId = entity.playlistId,
                    original = entity.original,
                    translation = entity.translation,
                    transcription = entity.transcription,
                    example = "" // примеры не приходят из сети
                )
            }
        }

    private suspend fun fetchAndCache() = withContext(ioDispatcher) {
        val response = api.getWords()
        val playlists = response.record.playlists
        val dao = db.wordDao()
        val playlistDao = db.playlistDao()

        val entities = playlists.flatMap { playlistDto ->
            val playlistId = ensurePlaylist(playlistDao, playlistDto.name)
            playlistDto.words.map { wordDto ->
                WordEntity(
                    id = 0,
                    playlistId = playlistId,
                    original = wordDto.original,
                    transcription = wordDto.transcription ?: "",
                    translation = wordDto.translation,
                    text = "",
                    isNew = true
                )
            }
        }

        if (entities.isNotEmpty()) {
            dao.clearAll()
            dao.insertAll(entities)
        }
    }

    private suspend fun ensurePlaylist(
        playlistDao: ru.sirius.grable.common.PlaylistDao,
        name: String
    ): Long {
        val existing = playlistDao.findByName(name)
        if (existing != null) return existing.id
        val insertedId = playlistDao.insert(PlaylistEntity(id = 0, name = name, description = null))
        return if (insertedId != -1L) insertedId else playlistDao.findByName(name)?.id ?: 0L
    }
}