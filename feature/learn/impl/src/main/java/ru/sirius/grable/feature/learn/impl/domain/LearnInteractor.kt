package ru.sirius.grable.feature.learn.impl.domain

import ru.sirius.fsrs.Rating
import java.util.Calendar

class LearnInteractor(
    private val repository: WordsRepository,
    private val fsrsService: FSRSService
) {
    suspend fun getSessionCards(
        playlistIds: List<Long>,
        target: Int,
        excludeIds: Set<Long> = emptySet()
    ): List<Word> {
        val due = repository.getDueWords(playlistIds, excludeIds)
        val remainingTarget = (target - due.size).coerceAtLeast(0)
        val newWords = repository.getNewWordsPool(playlistIds, excludeIds, remainingTarget)

        return (due + newWords).shuffled().distinctBy { it.id }
    }

    suspend fun saveProgress(word: Word, rating: Rating) {
        val isNewWord = word.fsrsData.reviewCount == 0
        val isRepeated = word.fsrsData.reviewCount > 0
        val isKnown = rating == Rating.Good || rating == Rating.Easy

        val nextState = fsrsService.calculateNext(word.fsrsData, rating)
        repository.saveState(nextState)

        repository.saveStatistics(
            wordId = word.id,
            isKnown = isKnown,
            isRepeated = isRepeated,
            isNewWord = isNewWord
        )
    }

    suspend fun markAsKnown(word: Word) {
        val nextState = fsrsService.calculateNext(word.fsrsData, Rating.Easy)
        repository.saveState(nextState)

        repository.saveStatistics(
            wordId = word.id,
            isKnown = true,
            isRepeated = false,
            isNewWord = word.fsrsData.reviewCount == 0
        )
    }

    suspend fun getLearnedNewWordsTodayCount(): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayStart = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val todayEnd = calendar.timeInMillis

        return repository.getLearnedNewWordsTodayCount(todayStart, todayEnd)
    }
}