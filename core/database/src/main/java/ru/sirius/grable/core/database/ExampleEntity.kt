package ru.sirius.grable.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "word_examples",
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"], childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExampleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val wordId: Long,
    val text: String,
    val translatedText: String?
)
