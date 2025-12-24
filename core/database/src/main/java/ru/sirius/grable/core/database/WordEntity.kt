package ru.sirius.grable.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "word",
    foreignKeys = [
        ForeignKey(
            PlaylistEntity::class,
            ["id"],
            ["playlistId"],
            ForeignKey.CASCADE
        )
    ]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val playlistId: Long,
    val original: String,
    val transcription: String,
    val translation: String,
    val text: String,
    val isNew: Boolean = true
)
