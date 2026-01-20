package ru.sirius.grable.feature.learn.impl.domain.mappers

import ru.sirius.fsrs.FlashCard
import ru.sirius.grable.core.database.ExampleEntity
import ru.sirius.grable.core.database.FullWordCardPOJO
import ru.sirius.grable.feature.learn.impl.domain.Word

fun FullWordCardPOJO.toDomain() = Word(
    id = word.id,
    playlistId = word.playlistId,
    original = word.original,
    translation = word.translation,
    transcription = word.transcription,
    examples = examples.map(ExampleEntity::toDomain),
    imageUrl = word.imageUrl,
    fsrsData = state?.toDomain() ?: FlashCard(id = this.word.id)
)