package ru.sirius.grable.common

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val description: String? = null
)
