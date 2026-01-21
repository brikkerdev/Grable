package ru.sirius.grable.feature.add_word.impl.domain

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sirius.grable.core.database.ExampleEntity
import ru.sirius.grable.core.database.PlaylistDao
import ru.sirius.grable.core.database.WordDao
import ru.sirius.grable.core.database.WordEntity
import ru.sirius.grable.feature.add_word.api.data.Example
import ru.sirius.grable.feature.add_word.api.domain.IAddWordRepository
import ru.sirius.grable.feature.playlist.api.Playlist

class AddWordRepository(
    private val wordDao: WordDao,
    private val playlistDao: PlaylistDao,
    private val context: Context
) : IAddWordRepository {

    override val word: Flow<String> = kotlinx.coroutines.flow.flowOf("")
    override val transcription: Flow<String> = kotlinx.coroutines.flow.flowOf("")
    override val translation: Flow<String> = kotlinx.coroutines.flow.flowOf("")
    override val examples: Flow<List<Example>> = kotlinx.coroutines.flow.flowOf(emptyList())

    override suspend fun setWord(value: String) {}
    override suspend fun setTranscription(value: String) {}
    override suspend fun setTranslation(value: String) {}
    override suspend fun addExample(example: Example) {}
    override suspend fun updateExamples(newList: List<Example>) {}
    override suspend fun generateFakeExamples(count: Int) {}
    override suspend fun clear() {}

    fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists()
            .map { entities ->
                entities.map { entity ->
                    val name = entity.name // TODO
                    Playlist(
                        id = entity.id,
                        name = name,
                        description = entity.description,
                        isSystem = entity.isSystem
                    )
                }
            }
    }

    suspend fun saveWord(
        playlistId: Long,
        original: String,
        transcription: String,
        translation: String,
        examples: List<Example>
    ): Long = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val wordId = wordDao.insert(
            WordEntity(
                playlistId = playlistId,
                original = original,
                transcription = transcription,
                translation = translation
            )
        )

        if (examples.isNotEmpty()) {
            val exampleEntities = examples.map { example ->
                ExampleEntity(
                    wordId = wordId,
                    text = example.english.trim(),
                    translatedText = example.russian.takeIf { it.isNotBlank() }?.trim()
                )
            }
            wordDao.insertExamples(exampleEntities)
        }

        wordId
    }
}

