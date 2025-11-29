package ru.sirius.grable.progress.data

data class DayStat(
    val date: String,
    val wordsCount: Int,
    val newWords: Int = 0,
    val repeatedWords: Int = 0
)