package ru.sirius.grable.feature.learn.impl.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sirius.grable.core.database.FSRSStateEntity
import ru.sirius.grable.core.database.FullWordCardPOJO
import ru.sirius.grable.core.database.StatisticsDao
import ru.sirius.grable.core.database.StatisticsEntity
import ru.sirius.grable.core.database.WordDao
import ru.sirius.grable.feature.learn.impl.domain.mappers.toDomain
import java.sql.Timestamp

class WordsRepository(
    private val wordDao: WordDao,
    private val statisticsDao: StatisticsDao
) {
    fun getWords(playlistId: Long): Flow<List<Word>> {
        return wordDao.observeWords(playlistId).map { entities ->
            entities.map(
                FullWordCardPOJO::toDomain
            )
        }
    }

    suspend fun getNewWordsPool(playlistIds: List<Long>, excludeIds: Set<Long> = emptySet(), limit: Int = 100): List<Word> = withContext(
        Dispatchers.IO) {
        wordDao.getNewWords(playlistIds)
            .filterNot { it.word.id in excludeIds }
            .take(limit)
            .map { it.toDomain() }
    }

    suspend fun getDueWords(playlistIds: List<Long>, excludeIds: Set<Long> = emptySet()): List<Word> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        wordDao.getDueWords(playlistIds, now)
            .filterNot { it.word.id in excludeIds }
            .map { it.toDomain() }
    }

    suspend fun saveState(state: FSRSStateEntity) = withContext(Dispatchers.IO) {
        wordDao.upsertState(state)
    }

    suspend fun saveStatistics(
        wordId: Long,
        isKnown: Boolean,
        isRepeated: Boolean,
        isNewWord: Boolean
    ) = withContext(Dispatchers.IO) {
        val statistic = StatisticsEntity(
            wordId = wordId,
            date = Timestamp(System.currentTimeMillis()),
            isKnown = isKnown,
            isRepeated = isRepeated,
            isNewWord = isNewWord
        )
        statisticsDao.insert(statistic)
    }

    suspend fun getLearnedNewWordsTodayCount(todayStart: Long, todayEnd: Long): Int = withContext(Dispatchers.IO) {
        statisticsDao.getLearnedNewWordsToday(todayStart, todayEnd)
    }
}

