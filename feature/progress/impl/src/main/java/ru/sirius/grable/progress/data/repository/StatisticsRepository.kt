package ru.sirius.grable.progress.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import ru.sirius.grable.core.database.StatisticsDao
import ru.sirius.grable.core.database.StatisticsEntity
import ru.sirius.grable.core.database.WordDao
import ru.sirius.grable.progress.data.DayStat
import ru.sirius.grable.progress.data.StatisticItem
import ru.sirius.grable.progress.data.StatisticsData
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.sql.Timestamp
import java.util.Locale
import kotlin.random.Random

interface StatisticsRepository {
    fun getStatistics(period: Int): Flow<StatisticsData>
    suspend fun addNewWord(wordId: Long)
    suspend fun markWordAsKnown(wordId: Long)
    suspend fun addWordRepetition(wordId: Long)
    suspend fun getLearningWordsCount(periodDays: Int): Int
    suspend fun generateMockData()
}

class StatisticsRepositoryImpl(
    private val statisticsDao: StatisticsDao,
    private val wordDao: WordDao
) : StatisticsRepository {

    override fun getStatistics(period: Int): Flow<StatisticsData> = flow {
        val startDate = if (period == -1) {
            Timestamp(0)
        } else {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -period)
            Timestamp(calendar.time.time)
        }

        val dailyStats = if (period == -1) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, 1970)
            calendar.set(Calendar.MONTH, Calendar.JANUARY)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            statisticsDao.getDailyStats(Timestamp(calendar.time.time))
        } else {
            statisticsDao.getDailyStats(startDate)
        }

        val totalKnown = statisticsDao.getTotalKnownCount()
        val totalRepeated = statisticsDao.getTotalRepeatedCount()
        val totalNewWords = statisticsDao.getTotalNewWordsCount()

        val periodStats = if (period == -1) {
            statisticsDao.getAll()
        } else {
            statisticsDao.getSinceDate(startDate)
        }
        
        val periodKnown = periodStats.count { it.isKnown }
        val periodRepeated = periodStats.count { it.isRepeated }
        val periodNewWords = periodStats.count { it.isNewWord }

        val learningCalendar = Calendar.getInstance()
        learningCalendar.add(Calendar.DAY_OF_YEAR, -7)
        val learningStartDate = Timestamp(learningCalendar.time.time)
        val learningWords = statisticsDao.getLearningWordsCount(learningStartDate)

        val domainDays = dailyStats.map { it.toDomain() }

        val statistics = listOf(
            StatisticItem("Заучено новых слов", totalNewWords, periodNewWords),
            StatisticItem("Повторено слов", totalRepeated, periodRepeated),
            StatisticItem("Всего выучено", totalKnown, periodKnown)
        )

        val periodTitle = when (period) {
            7 -> "7 дней"
            30 -> "30 дней"
            90 -> "90 дней"
            -1 -> "Все время"
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

    override suspend fun generateMockData() = withContext(Dispatchers.IO) {
        val words = wordDao.getAll()
        if (words.isEmpty()) return@withContext

        val calendar = Calendar.getInstance()
        val statistics = mutableListOf<StatisticsEntity>()

        for (dayOffset in 0 until 30) {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -dayOffset)
            
            val wordsForDay = words.shuffled().take(Random.nextInt(1, minOf(5, words.size + 1)))
            
            wordsForDay.forEach { word ->
                val isNewWord = Random.nextBoolean()
                val isKnown = if (isNewWord) Random.nextBoolean() else true
                val isRepeated = if (isKnown && !isNewWord) Random.nextBoolean() else false
                
                val date = Timestamp(calendar.timeInMillis)
                statistics.add(
                    StatisticsEntity(
                        wordId = word.id,
                        date = date,
                        isKnown = isKnown,
                        isRepeated = isRepeated,
                        isNewWord = isNewWord
                    )
                )
            }
        }

        statistics.forEach { statisticsDao.insert(it) }
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