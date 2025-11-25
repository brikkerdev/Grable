package ru.sirius.grable.progress.data

data class DayStat(
    val date: String,
    val wordsCount: Int,
    val maxWords: Int = 0
)