package ru.sirius.grable.progress.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.sirius.grable.progress.data.DayStat
import ru.sirius.grable.progress.data.StatisticItem
import ru.sirius.grable.progress.data.StatisticsData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface StatisticsRepository {
    fun getStatistics(period: Int): Flow<StatisticsData>
    suspend fun updateDailyStats(newWords: Int, repeatedWords: Int)
    suspend fun getLearningWordsCount(): Int
    suspend fun addLearningWord()
    suspend fun removeLearningWord()
}

class StatisticsRepositoryImpl : StatisticsRepository {

    // Имитация базы данных или файлового хранилища
    private val dailyStats = mutableListOf<DayStat>()
    private var learningWordsCount = 0
    private var totalWordsLearned = 754
    private var totalWordsRepeated = 754
    private var totalFullyLearned = 284
    private var totalKnownWords = 275

    init {
        // Инициализация тестовыми данными
        initTestData()
    }

    override fun getStatistics(period: Int): Flow<StatisticsData> = flow {
        val filteredDays = getFilteredDays(period)
        val learningNow = getLearningWordsCount()

        val statistics = listOf(
            StatisticItem("Заучено новых слов", totalWordsLearned, calculatePeriodCount(filteredDays) { it.newWords }),
            StatisticItem("Повторено (уникальных)", totalWordsRepeated, calculatePeriodCount(filteredDays) { it.repeatedWords }),
            StatisticItem("Полностью выучено", totalFullyLearned, 44),
            StatisticItem("Уже известные", totalKnownWords, 17)
        )

        val periodTitle = when (period) {
            7 -> "7 дней"
            30 -> "30 дней"
            90 -> "90 дней"
            else -> "Все время"
        }

        emit(StatisticsData(periodTitle, learningNow, filteredDays, statistics, period))
    }

    override suspend fun updateDailyStats(newWords: Int, repeatedWords: Int) {
        val currentDate = getCurrentDate()

        // Обновляем общую статистику
        totalWordsLearned += newWords
        totalWordsRepeated += repeatedWords

        // Обновляем дневную статистику
        val existingStat = dailyStats.find { it.date == currentDate }
        if (existingStat != null) {
            dailyStats.remove(existingStat)
            dailyStats.add(
                existingStat.copy(
                    wordsCount = existingStat.wordsCount + newWords + repeatedWords,
                    newWords = existingStat.newWords + newWords,
                    repeatedWords = existingStat.repeatedWords + repeatedWords
                )
            )
        } else {
            dailyStats.add(
                DayStat(
                    date = currentDate,
                    wordsCount = newWords + repeatedWords,
                    newWords = newWords,
                    repeatedWords = repeatedWords
                )
            )
        }
    }

    override suspend fun getLearningWordsCount(): Int {
        return learningWordsCount
    }

    override suspend fun addLearningWord() {
        learningWordsCount++
    }

    override suspend fun removeLearningWord() {
        if (learningWordsCount > 0) {
            learningWordsCount--
        }
    }

    private fun getFilteredDays(period: Int): List<DayStat> {
        if (period == -1) return dailyStats

        // Для простоты возвращаем последние N дней
        return dailyStats.takeLast(period).ifEmpty { dailyStats }
    }

    private fun calculatePeriodCount(days: List<DayStat>, selector: (DayStat) -> Int): Int {
        return days.sumOf { selector(it) }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("d MMM", Locale.getDefault()).format(Date())
    }

    private fun initTestData() {
        dailyStats.addAll(
            listOf(
                DayStat("3 мар", 6, 4, 2),
                DayStat("4 мар", 22, 15, 7),
                DayStat("5 мар", 18, 12, 6),
                DayStat("6 мар", 16, 10, 6),
                DayStat("7 мар", 4, 2, 2),
                DayStat("8 мар", 3, 2, 1),
                DayStat("9 мар", 7, 5, 2),
                DayStat("10 мар", 6, 4, 2),
                DayStat("11 мар", 22, 15, 7),
                DayStat("12 мар", 18, 12, 6),
                DayStat("13 мар", 16, 10, 6),
                DayStat("14 мар", 4, 2, 2),
                DayStat("15 мар", 3, 2, 1),
                DayStat("16 мар", 7, 5, 2)
            )
        )
    }
}