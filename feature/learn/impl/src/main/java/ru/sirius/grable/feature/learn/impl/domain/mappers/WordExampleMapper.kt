package ru.sirius.grable.feature.learn.impl.domain.mappers

import ru.sirius.grable.core.database.ExampleEntity
import ru.sirius.grable.feature.learn.impl.domain.WordExample

fun ExampleEntity.toDomain() = WordExample(
    id = id,
    wordId = wordId,
    text = text
)