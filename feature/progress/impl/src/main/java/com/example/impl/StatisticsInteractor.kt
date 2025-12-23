package ru.sirius.grable.progress

import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.progress.data.StatisticsData
import ru.sirius.grable.progress.data.repository.StatisticsRepository

class StatisticsInteractor(
    private val repository: StatisticsRepository
) {

    fun getStatistics(period: Int): Flow<StatisticsData> {
        return repository.getStatistics(period)
    }

    suspend fun addNewWord(wordId: Long) {
        repository.addNewWord(wordId)
    }

    suspend fun markWordAsKnown(wordId: Long) {
        repository.markWordAsKnown(wordId)
    }

    suspend fun addWordRepetition(wordId: Long) {
        repository.addWordRepetition(wordId)
    }

    suspend fun getLearningWordsCount(): Int {
        return repository.getLearningWordsCount(7)
    }

    suspend fun getTodayStats(): StatisticsData? {
        return null
    }

    suspend fun getWordProgress(wordId: Long): Map<String, Any> {
        return emptyMap()
    }
}