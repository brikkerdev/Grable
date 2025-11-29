package ru.sirius.grable.progress

import kotlinx.coroutines.flow.Flow
import ru.sirius.grable.progress.data.StatisticsData
import ru.sirius.grable.progress.data.repository.StatisticsRepository
import ru.sirius.grable.progress.data.repository.StatisticsRepositoryImpl

class StatisticsInteractor(
    private val repository: StatisticsRepository = StatisticsRepositoryImpl()
) {

    fun getStatistics(period: Int): Flow<StatisticsData> {
        return repository.getStatistics(period)
    }

    suspend fun updateDailyStats(newWords: Int, repeatedWords: Int) {
        repository.updateDailyStats(newWords, repeatedWords)
    }

    suspend fun getLearningWordsCount(): Int {
        return repository.getLearningWordsCount()
    }

    suspend fun addLearningWord() {
        repository.addLearningWord()
    }

    suspend fun removeLearningWord() {
        repository.removeLearningWord()
    }
}