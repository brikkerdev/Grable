package ru.sirius.grable.common

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlist ORDER BY id ASC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
}