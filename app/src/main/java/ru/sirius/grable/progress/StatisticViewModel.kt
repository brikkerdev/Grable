package ru.sirius.grable.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.sirius.grable.progress.data.StatisticsData

class StatisticsViewModel() : ViewModel() {

    private val interactor = StatisticsInteractor()
    private val _selectedPeriod = MutableStateFlow(7)
    val selectedPeriod = _selectedPeriod.asStateFlow()

    val statisticsData = _selectedPeriod
        .flatMapLatest { period ->
            interactor.getStatistics(period)
        }

    private val _learningWordsCount = MutableStateFlow(0)
    val learningWordsCount = _learningWordsCount.asStateFlow()

    init {
        loadLearningWordsCount()
    }

    fun setPeriod(period: Int) {
        _selectedPeriod.value = period
    }

    fun updateDailyStats(newWords: Int, repeatedWords: Int) {
        viewModelScope.launch {
            interactor.updateDailyStats(newWords, repeatedWords)
        }
    }

    fun addLearningWord() {
        viewModelScope.launch {
            interactor.addLearningWord()
            loadLearningWordsCount()
        }
    }

    fun removeLearningWord() {
        viewModelScope.launch {
            interactor.removeLearningWord()
            loadLearningWordsCount()
        }
    }

    private fun loadLearningWordsCount() {
        viewModelScope.launch {
            _learningWordsCount.value = interactor.getLearningWordsCount()
        }
    }
}