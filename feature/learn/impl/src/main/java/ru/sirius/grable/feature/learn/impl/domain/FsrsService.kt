package ru.sirius.grable.feature.learn.impl.domain

import ru.sirius.fsrs.CardPhase
import ru.sirius.fsrs.Constants
import ru.sirius.fsrs.FSRS
import ru.sirius.fsrs.FlashCard
import ru.sirius.fsrs.Rating
import ru.sirius.grable.core.database.FSRSStateEntity


class FSRSService {
    private val fsrs = FSRS(
        Constants.retention,
        Constants.params,
    )

    fun calculateNext(card: FlashCard, rating: Rating): FSRSStateEntity {
        val grades = fsrs.calculate(card)
        val selected = grades.first { it.choice == rating }

        val isKnown = rating == Rating.Good || rating == Rating.Easy
        val isNewWord = card.reviewCount == 0
        
        val phase = when {
            isNewWord && isKnown -> CardPhase.Review.value
            selected.interval > 0 -> CardPhase.Review.value
            else -> CardPhase.ReLearning.value
        }

        return FSRSStateEntity(
            wordId = card.id,
            stability = selected.stability,
            difficulty = selected.difficulty,
            interval = selected.interval,
            dueDateMillis = System.currentTimeMillis() + selected.durationMillis,
            lastReviewMillis = System.currentTimeMillis(),
            reviewCount = card.reviewCount + 1,
            phase = phase
        )
    }
}