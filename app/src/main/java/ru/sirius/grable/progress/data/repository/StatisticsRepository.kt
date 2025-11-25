package ru.sirius.grable.progress.data.repository

import ru.sirius.grable.progress.data.DayStat
import ru.sirius.grable.progress.data.StatisticItem
import ru.sirius.grable.progress.data.StatisticsData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class StatisticsRepository {

    fun getStatistics(period: Int): StatisticsData {
        return when (period) {
            7 -> getWeeklyStatistics()
            30 -> getMonthlyStatistics()
            90 -> getQuarterlyStatistics()
            else -> getAllTimeStatistics()
        }
    }

    private fun getWeeklyStatistics(): StatisticsData {
        val days = listOf(
            DayStat("3 мар", 6),
            DayStat("4 мар", 22),
            DayStat("5 мар", 18),
            DayStat("6 мар", 16),
            DayStat("7 мар", 4),
            DayStat("8 мар", 3),
            DayStat("9 мар", 7)
        )

        val statistics = listOf(
            StatisticItem("Заучено новых слов", 754, 45),
            StatisticItem("Повторено (уникальных)", 754, 93),
            StatisticItem("Полностью выучено", 284, 44),
            StatisticItem("Уже известные", 275, 17)
        )

        return StatisticsData("7 дней", 0, days, statistics)
    }

    private fun getMonthlyStatistics(): StatisticsData {
        // Генерация данных за 30 дней
        val days = generateDaysData(30)

        val statistics = listOf(
            StatisticItem("Заучено новых слов", 2150, 320),
            StatisticItem("Повторено (уникальных)", 2890, 540),
            StatisticItem("Полностью выучено", 890, 210),
            StatisticItem("Уже известные", 750, 95)
        )

        return StatisticsData("30 дней", 12, days, statistics)
    }

    private fun getQuarterlyStatistics(): StatisticsData {
        val days = generateDaysData(90, isQuarterly = true)

        val statistics = listOf(
            StatisticItem("Заучено новых слов", 5840, 890),
            StatisticItem("Повторено (уникальных)", 7520, 1240),
            StatisticItem("Полностью выучено", 2450, 560),
            StatisticItem("Уже известные", 1980, 240)
        )

        return StatisticsData("90 дней", 8, days, statistics)
    }

    private fun getAllTimeStatistics(): StatisticsData {
        val days = generateDaysData(180, isQuarterly = true)

        val statistics = listOf(
            StatisticItem("Заучено новых слов", 12540, 2150),
            StatisticItem("Повторено (уникальных)", 18420, 3240),
            StatisticItem("Полностью выучено", 6520, 1240),
            StatisticItem("Уже известные", 4250, 580)
        )

        return StatisticsData("Все время", 15, days, statistics)
    }

    private fun generateDaysData(daysCount: Int, isQuarterly: Boolean = false): List<DayStat> {
        val days = mutableListOf<DayStat>()
        val random = Random(System.currentTimeMillis())

        val dateFormat = if (isQuarterly) SimpleDateFormat("dd MMM", Locale.getDefault())
        else SimpleDateFormat("d MMM", Locale.getDefault())

        val calendar = Calendar.getInstance()

        for (i in daysCount downTo 1) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val date = dateFormat.format(calendar.time)
            val wordsCount = random.nextInt(5, 25)
            days.add(0, DayStat(date, wordsCount))
        }

        return days
    }
}