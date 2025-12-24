package ru.sirius.grable.core.database

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sirius.network.words_api.NetworkModule
import ru.sirius.network.words_api.WordDto
import ru.sirius.network.words_api.PlaylistDto

class DataSyncService(
    private val database: AppDatabase
) {
    private val wordsApiService = NetworkModule.createWordsApiService()
    private val playlistDao = database.playlistDao()
    private val wordDao = database.wordDao()

    suspend fun syncDataFromApi() {
        withContext(Dispatchers.IO) {
            try {
                Log.d("DataSyncService", "Starting data sync from API")
                val response = wordsApiService.getWords()
                val playlists = response.record.playlists

                playlists.forEach { playlistDto ->
                    syncPlaylist(playlistDto)
                }

                Log.d("DataSyncService", "Data sync completed successfully")
            } catch (e: Exception) {
                Log.e("DataSyncService", "Error syncing data from API", e)
                throw e
            }
        }
    }

    private suspend fun syncPlaylist(playlistDto: PlaylistDto) {
        var playlistEntity = playlistDao.findByName(playlistDto.name)
        
        if (playlistEntity == null) {
            playlistEntity = PlaylistEntity(
                id = 0,
                name = playlistDto.name,
                description = playlistDto.name
            )
            val playlistId = playlistDao.insert(playlistEntity)
            playlistEntity = playlistEntity.copy(id = playlistId)
        }

        if (playlistDto.words.isNotEmpty()) {
            val wordEntities = playlistDto.words.map { wordDto ->
                WordEntity(
                    id = 0,
                    playlistId = playlistEntity.id,
                    original = wordDto.original,
                    translation = wordDto.translation,
                    transcription = wordDto.transcription ?: "",
                    text = wordDto.original,
                    isNew = true
                )
            }
            wordDao.insertAll(wordEntities)
        }
    }

    suspend fun hasData(): Boolean = withContext(Dispatchers.IO) {
        val count = wordDao.countAll()
        count > 0
    }
}

