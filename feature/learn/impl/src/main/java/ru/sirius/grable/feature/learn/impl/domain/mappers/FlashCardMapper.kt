package ru.sirius.grable.feature.learn.impl.domain.mappers

import ru.sirius.fsrs.FlashCard
import ru.sirius.grable.core.database.FSRSStateEntity
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.Long

fun FSRSStateEntity.toDomain() = FlashCard(
    id = this.wordId,
    stability = this.stability,
    difficulty = this.difficulty,
    interval = this.interval,
    dueDate = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this.dueDateMillis), ZoneId.systemDefault()
    ),
    lastReview = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this.lastReviewMillis), ZoneId.systemDefault()
    ),
    reviewCount = this.reviewCount,
    phase = this.phase
)