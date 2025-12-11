package ru.sirius.grable.common

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExampleDao {
    @Insert
    suspend fun insertExample(example: ExampleEntity): Long

    @Query("SELECT * FROM example ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomExamples(count: Int): List<ExampleEntity>
}