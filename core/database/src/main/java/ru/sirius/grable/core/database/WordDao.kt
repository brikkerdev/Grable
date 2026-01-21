package ru.sirius.grable.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("SELECT * FROM words WHERE playlistId = :playlistId ORDER BY id ASC")
    fun observeWords(playlistId: Long): Flow<List<FullWordCardPOJO>>

    @Query("SELECT COUNT(*) FROM words")
    suspend fun countAll(): Int

    @Query("SELECT * FROM words")
    suspend fun getAll(): List<WordEntity>

    @Query("DELETE FROM words")
    suspend fun clearAll()

    @Query("DELETE FROM words WHERE id = :wordId")
    suspend fun deleteWord(wordId: Long)

    @Transaction
    @Query("SELECT * FROM words WHERE playlistId IN (:playlistIds) AND id NOT IN (SELECT wordId FROM word_fsrs_states)")
    suspend fun getNewWords(playlistIds: List<Long>): List<FullWordCardPOJO>

    @Transaction
    @Query("SELECT * FROM words JOIN word_fsrs_states ON words.id = word_fsrs_states.wordId WHERE words.playlistId IN (:playlistIds) AND word_fsrs_states.dueDateMillis <= :now AND (word_fsrs_states.reviewCount = 0 OR word_fsrs_states.stability <= 5.0)")
    suspend fun getDueWords(playlistIds: List<Long>, now: Long): List<FullWordCardPOJO>

    @Upsert
    suspend fun upsertState(state: FSRSStateEntity)

    @Update
    suspend fun updateWord(word: WordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: WordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamples(examples: List<ExampleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)

}

