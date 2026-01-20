package ru.sirius.grable.feature.home.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.sirius.grable.core.database.PlaylistDao
import ru.sirius.grable.core.database.StatisticsDao
import ru.sirius.grable.core.database.WordDao
import java.util.Calendar

class HomeViewModel(
    private val wordDao: WordDao,
    private val statisticsDao: StatisticsDao,
    private val playlistDao: PlaylistDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val allPlaylists = playlistDao.getAllPlaylistIds()
                val dueCount = wordDao.getDueWords(allPlaylists, System.currentTimeMillis()).size
                
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val todayStart = calendar.timeInMillis

                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                val todayEnd = calendar.timeInMillis

                val learnedToday = statisticsDao.getLearnedNewWordsToday(todayStart, todayEnd)
                
                _uiState.value = _uiState.value.copy(
                    dueWordsCount = dueCount,
                    learnedNewWordsToday = learnedToday
                )
            } catch (e: Exception) {
            }
        }
    }

    fun refreshData() {
        loadData()
    }
}

data class HomeUiState(
    val dueWordsCount: Int = 0,
    val learnedNewWordsToday: Int = 0
)

