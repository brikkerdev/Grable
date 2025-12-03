package ru.sirius.grable.common

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "examples")
data class ExampleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long
)