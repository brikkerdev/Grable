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

        return FSRSStateEntity(
            wordId = card.id,
            stability = selected.stability,
            difficulty = selected.difficulty,
            interval = selected.interval,
            // durationMillis из Grade для следующей даты
            dueDateMillis = System.currentTimeMillis() + selected.durationMillis,
            lastReviewMillis = System.currentTimeMillis(),
            reviewCount = card.reviewCount + 1,
            phase = if (selected.interval > 0) CardPhase.Review.value else CardPhase.ReLearning.value
        )
    }
}