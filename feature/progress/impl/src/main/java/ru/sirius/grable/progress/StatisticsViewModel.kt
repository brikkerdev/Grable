package ru.sirius.grable.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.sirius.grable.progress.data.StatisticsData

class StatisticsViewModel(
    private val interactor: StatisticsInteractor
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(7)
    val selectedPeriod = _selectedPeriod.asStateFlow()

    val statisticsData = _selectedPeriod.flatMapLatest { period ->
        interactor.getStatistics(period)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        StatisticsData("7 дней", 0, emptyList(), emptyList(), 7)
    )

    private val _learningWordsCount = MutableStateFlow(0)
    val learningWordsCount = _learningWordsCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        loadLearningWordsCount()
    }

    fun setPeriod(period: Int) {
        _selectedPeriod.value = period
    }

    fun addNewWord(wordId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                interactor.addNewWord(wordId)
                loadLearningWordsCount()
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при добавлении слова: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markWordAsKnown(wordId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                interactor.markWordAsKnown(wordId)
                loadLearningWordsCount()
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при отметке слова: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addWordRepetition(wordId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                interactor.addWordRepetition(wordId)
                loadLearningWordsCount()
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при добавлении повторения: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun refreshData() {
        loadLearningWordsCount()
    }

    private fun loadLearningWordsCount() {
        viewModelScope.launch {
            try {
                _learningWordsCount.value = interactor.getLearningWordsCount()
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки статистики: ${e.message}"
            }
        }
    }
}

// Исправленный Factory
class StatisticsViewModelFactory(
    private val interactor: StatisticsInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatisticsViewModel(interactor) as T
    }
}