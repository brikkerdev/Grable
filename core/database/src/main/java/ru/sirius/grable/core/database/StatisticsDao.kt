package ru.sirius.grable.core.database

import androidx.room.*
import java.sql.Timestamp
import java.util.*


@Dao
interface StatisticsDao {

    @Insert
    suspend fun insert(statistic: StatisticsEntity): Long

    @Update
    suspend fun update(statistic: StatisticsEntity)

    @Query("SELECT * FROM statistics")
    suspend fun getAll(): List<StatisticsEntity>

    @Query("SELECT * FROM statistics WHERE date >= :startDate")
    suspend fun getSinceDate(startDate: Timestamp): List<StatisticsEntity>

    @Query("SELECT COUNT(*) FROM statistics WHERE isKnown = 1")
    suspend fun getTotalKnownCount(): Int

    @Query("SELECT COUNT(*) FROM statistics WHERE isRepeated = 1")
    suspend fun getTotalRepeatedCount(): Int

    @Query("SELECT COUNT(*) FROM statistics WHERE isNewWord = 1")
    suspend fun getTotalNewWordsCount(): Int

    @Query("""
        SELECT 
            strftime('%Y-%m-%d', date/1000, 'unixepoch') as day,
            COUNT(*) as total_count,
            SUM(CASE WHEN isNewWord = 1 THEN 1 ELSE 0 END) as new_words,
            SUM(CASE WHEN isRepeated = 1 THEN 1 ELSE 0 END) as repeated_words,
            SUM(CASE WHEN isKnown = 1 THEN 1 ELSE 0 END) as known_words
        FROM statistics 
        WHERE date >= :startDate 
        GROUP BY day 
        ORDER BY day DESC
    """)
    suspend fun getDailyStats(startDate: Timestamp): List<DailyStat>

    @Query("""
        SELECT COUNT(DISTINCT wordId) 
        FROM statistics 
        WHERE date >= :startDate 
        AND isNewWord = 1 
        AND isKnown = 0
    """)
    suspend fun getLearningWordsCount(startDate: Timestamp): Int
}

data class DailyStat(
    val day: String, // Дата в формате "YYYY-MM-DD"
    val total_count: Int,
    val new_words: Int,
    val repeated_words: Int,
    val known_words: Int
)