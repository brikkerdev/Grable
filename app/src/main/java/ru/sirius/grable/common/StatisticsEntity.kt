package ru.sirius.grable.common

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(
    tableName = "statistics",
    foreignKeys = [
        ForeignKey(
            WordEntity::class,
            ["id"],
            ["wordId"],
            ForeignKey.CASCADE
        )
    ]
)
data class StatisticsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wordId: Long,
    val date: Timestamp,
    val isKnown: Boolean
)
