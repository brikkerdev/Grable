package ru.sirius.grable.statistics.data.model

import ru.sirius.grable.core.database.DailyStat

// Data Transfer Objects для работы с БД
data class DailyStatsResult(
    val day: String,
    val totalCount: Int,
    val newWords: Int,
    val repeatedWords: Int,
    val knownWords: Int
)

fun DailyStat.toResult(): DailyStatsResult {
    return DailyStatsResult(
        day = this.day,
        totalCount = this.total_count,
        newWords = this.new_words,
        repeatedWords = this.repeated_words,
        knownWords = this.known_words
    )
}