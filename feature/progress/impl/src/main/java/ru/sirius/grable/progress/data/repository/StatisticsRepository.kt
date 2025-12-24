package ru.sirius.grable.progress.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.sirius.grable.core.database.StatisticsDao
import ru.sirius.grable.core.database.StatisticsEntity
import ru.sirius.grable.progress.data.DayStat
import ru.sirius.grable.progress.data.StatisticItem
import ru.sirius.grable.progress.data.StatisticsData
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.sql.Timestamp
import java.util.Locale

interface StatisticsRepository {
    fun getStatistics(period: Int): Flow<StatisticsData>
    suspend fun addNewWord(wordId: Long)
    suspend fun markWordAsKnown(wordId: Long)
    suspend fun addWordRepetition(wordId: Long)
    suspend fun getLearningWordsCount(periodDays: Int): Int
}

class StatisticsRepositoryImpl(
    private val statisticsDao: StatisticsDao
) : StatisticsRepository {

    override fun getStatistics(period: Int): Flow<StatisticsData> = flow {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -period)
        val startDate = Timestamp(calendar.time.time)

        // Получаем данные из БД
        val dailyStats = statisticsDao.getDailyStats(startDate)
        val totalKnown = statisticsDao.getTotalKnownCount()
        val totalRepeated = statisticsDao.getTotalRepeatedCount()
        val totalNewWords = statisticsDao.getTotalNewWordsCount()

        // Получаем статистику за период
        val periodStats = statisticsDao.getSinceDate(startDate)
        val periodKnown = periodStats.count { it.isKnown }
        val periodRepeated = periodStats.count { it.isRepeated }
        val periodNewWords = periodStats.count { it.isNewWord }

        // Получаем изучаемые слова
        val learningCalendar = Calendar.getInstance()
        learningCalendar.add(Calendar.DAY_OF_YEAR, -7)
        val learningStartDate = Timestamp(learningCalendar.time.time)
        val learningWords = statisticsDao.getLearningWordsCount(learningStartDate)

        // Конвертируем DailyStat в DayStat
        val domainDays = dailyStats.map { it.toDomain() }

        // Формируем статистику
        val statistics = listOf(
            StatisticItem("Заучено новых слов", totalNewWords, periodNewWords),
            StatisticItem("Повторено слов", totalRepeated, periodRepeated),
            StatisticItem("Всего выучено", totalKnown, periodKnown),
            StatisticItem("Уже известные", totalKnown, 0)
        )

        val periodTitle = when (period) {
            7 -> "7 дней"
            30 -> "30 дней"
            90 -> "90 дней"
            else -> "Все время"
        }

        emit(StatisticsData(periodTitle, learningWords, domainDays, statistics, period))
    }.flowOn(Dispatchers.IO)

    override suspend fun addNewWord(wordId: Long) {
        val calendar = Calendar.getInstance()
        val statistic = StatisticsEntity(
            wordId = wordId,
            date = Timestamp(calendar.time.time),
            isKnown = false,
            isRepeated = false,
            isNewWord = true
        )
        statisticsDao.insert(statistic)
    }

    override suspend fun markWordAsKnown(wordId: Long) {
        // Находим все записи для этого слова
        val allStats = statisticsDao.getAll()
        val wordStats = allStats.filter { it.wordId == wordId }

        // Помечаем как выученное
        val existingStat = wordStats.find { it.isNewWord && !it.isKnown }

        existingStat?.let {
            statisticsDao.update(it.copy(isKnown = true))
        } ?: run {
            val calendar = Calendar.getInstance()
            val newStat = StatisticsEntity(
                wordId = wordId,
                date = Timestamp(calendar.time.time),
                isKnown = true,
                isRepeated = false,
                isNewWord = false
            )
            statisticsDao.insert(newStat)
        }
    }

    override suspend fun addWordRepetition(wordId: Long) {
        val calendar = Calendar.getInstance()
        val repetitionStat = StatisticsEntity(
            wordId = wordId,
            date = Timestamp(calendar.time.time),
            isKnown = true,
            isRepeated = true,
            isNewWord = false
        )
        statisticsDao.insert(repetitionStat)
    }

    override suspend fun getLearningWordsCount(periodDays: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -periodDays)
        val startDate = Timestamp(calendar.time.time)
        return statisticsDao.getLearningWordsCount(startDate)
    }

    private fun ru.sirius.grable.core.database.DailyStat.toDomain(): DayStat {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val displayFormat = SimpleDateFormat("d MMM", Locale.getDefault())
            val date = dateFormat.parse(this.day)
            val calendar = Calendar.getInstance()

            DayStat(
                date = displayFormat.format(date ?: Timestamp(calendar.time.time)),
                wordsCount = this.total_count,
                newWords = this.new_words,
                repeatedWords = this.repeated_words
            )
        } catch (e: Exception) {
            DayStat(
                date = this.day,
                wordsCount = this.total_count,
                newWords = this.new_words,
                repeatedWords = this.repeated_words
            )
        }
    }
}