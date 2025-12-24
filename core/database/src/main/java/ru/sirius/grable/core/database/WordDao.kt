package ru.sirius.grable.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("SELECT * FROM word WHERE playlistId = :playlistId ORDER BY id ASC")
    fun observeWords(playlistId: Long): Flow<List<WordEntity>>

    @Query("SELECT COUNT(*) FROM word")
    suspend fun countAll(): Int

    @Query("SELECT * FROM word")
    suspend fun getAll(): List<WordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)

    @Query("DELETE FROM word")
    suspend fun clearAll()
}

