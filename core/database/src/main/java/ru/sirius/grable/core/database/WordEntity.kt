package ru.sirius.grable.core.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

//@Entity(
//    tableName = "word",
//    foreignKeys = [
//        ForeignKey(
//            PlaylistEntity::class,
//            ["id"],
//            ["playlistId"],
//            ForeignKey.CASCADE
//        )
//    ]
//)
//data class WordEntity(
//    @PrimaryKey(autoGenerate = true) val id: Long,
//    val playlistId: Long,
//    val original: String,
//    val translation: String,
//    // Поля для FSRS:
//    val stability: Double = 0.0,
//    val difficulty: Double = 0.0,
//    val elapsedDays: Long = 0,
//    val scheduledDays: Long = 0,
//    val reps: Int = 0,
//    val lapses: Int = 0,
//    val state: Int = 0, // 0: New, 1: Learning, 2: Review, 3: Relearning
//    val lastReview: Long? = null, // Timestamp
//    val nextReview: Long? = null,  // Timestamp (для фильтрации)
//    val isNew: Boolean = true
//)

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playlistId: Long,
    val original: String,
    val transcription: String,
    val translation: String,
    val imageUrl: String? = null
)


@Entity(tableName = "word_fsrs_states")
data class FSRSStateEntity(
    @PrimaryKey val wordId: Long,
    val stability: Double,
    val difficulty: Double,
    val interval: Int,
    val dueDateMillis: Long,
    val lastReviewMillis: Long,
    val reviewCount: Int,
    val phase: Int
)

data class FullWordCardPOJO(
    @Embedded val word: WordEntity,
    @Relation(parentColumn = "id", entityColumn = "wordId") val state: FSRSStateEntity?,
    @Relation(parentColumn = "id", entityColumn = "wordId") val examples: List<ExampleEntity>
)