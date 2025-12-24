package ru.sirius.grable.progress.data

data class StatisticsData(
    val periodTitle: String,
    val learningNow: Int,
    val days: List<DayStat>,
    val statistics: List<StatisticItem>,
    val period: Int
)