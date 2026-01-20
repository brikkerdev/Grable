package ru.sirius.grable.feature.learn.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.sirius.fsrs.Rating
import ru.sirius.grable.feature.learn.impl.domain.LearnInteractor
import ru.sirius.grable.feature.learn.impl.domain.Word

class LearnViewModel(
    private val learnInteractor: LearnInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow<LearnUiState>(LearnUiState.Loading)
    val uiState: StateFlow<LearnUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LearnEvent>()
    val events = _events.asSharedFlow()

    private var learnedCount = 0
    private var reviewedCount = 0
    private var targetCount = 0
    private var playlistIds: List<Long> = emptyList()
    private val reviewedInSession = mutableSetOf<Long>()
    private var currentCards = listOf<Word>()

    fun startSession(playlistIds: List<Long>, target: Int, initialLearnedCount: Int = 0, initialReviewedCount: Int = 0) {
        this.playlistIds = playlistIds
        this.targetCount = target
        this.learnedCount = initialLearnedCount
        this.reviewedCount = initialReviewedCount

        viewModelScope.launch {
            try {
                val sessionCards = learnInteractor.getSessionCards(playlistIds, target, reviewedInSession)
                currentCards = sessionCards
                if (sessionCards.isEmpty()) {
                    _uiState.value = LearnUiState.Empty
                } else {
                    _uiState.value = LearnUiState.Content(
                        cards = sessionCards,
                        learnedProgress = learnedCount,
                        targetProgress = target
                    )
                }
            } catch (e: Exception) {
                _uiState.value = LearnUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun removeCurrentCard() {
        val current = _uiState.value
        if (current is LearnUiState.Content && current.cards.isNotEmpty()) {
            val newCards = current.cards.drop(1)
            currentCards = newCards
            
            if (newCards.isEmpty()) {
                loadMoreCards()
            } else {
                _uiState.value = current.copy(cards = newCards)
            }
        }
    }

    private fun loadMoreCards() {
        viewModelScope.launch {
            try {
                val sessionCards = learnInteractor.getSessionCards(playlistIds, targetCount, reviewedInSession)
                currentCards = sessionCards
                if (sessionCards.isEmpty()) {
                    if (learnedCount >= targetCount) {
                        _events.emit(LearnEvent.DailyGoalCompleted(learnedCount, reviewedCount))
                    } else {
                        _uiState.value = LearnUiState.Empty
                        _events.emit(LearnEvent.SessionFinished)
                    }
                } else {
                    _uiState.value = LearnUiState.Content(
                        cards = sessionCards,
                        learnedProgress = learnedCount,
                        targetProgress = targetCount
                    )
                }
            } catch (e: Exception) {
                _uiState.value = LearnUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onSwipe(word: Word, rating: Rating) {
        viewModelScope.launch {
            reviewedInSession.add(word.id)
            
            learnInteractor.saveProgress(word, rating)

            if (word.fsrsData.reviewCount == 0 && (rating == Rating.Good || rating == Rating.Easy)) {
                learnedCount++
            } else if (word.fsrsData.reviewCount > 0) {
                reviewedCount++
            }

            removeCurrentCard()
            updateProgress()

            if (learnedCount >= targetCount) {
                _events.emit(LearnEvent.DailyGoalCompleted(learnedCount, reviewedCount))
            }
        }
    }

    fun onMarkAsKnown(word: Word) {
        viewModelScope.launch {
            reviewedInSession.add(word.id)
            learnInteractor.markAsKnown(word)
            
            if (word.fsrsData.reviewCount == 0) {
                learnedCount++
            } else {
                reviewedCount++
            }

            removeCurrentCard()
            updateProgress()
            _events.emit(LearnEvent.CardMarkedAsKnown)

            if (learnedCount >= targetCount) {
                _events.emit(LearnEvent.DailyGoalCompleted(learnedCount, reviewedCount))
            }
        }
    }

    private fun updateProgress() {
        val current = _uiState.value
        if (current is LearnUiState.Content) {
            _uiState.value = current.copy(learnedProgress = learnedCount)
        }
    }
}

sealed class LearnUiState {
    object Loading : LearnUiState()
    object Empty : LearnUiState()
    data class Content(
        val cards: List<Word>,
        val learnedProgress: Int,
        val targetProgress: Int
    ) : LearnUiState()
    data class Error(val message: String) : LearnUiState()
}

sealed class LearnEvent {
    object SessionFinished : LearnEvent()
    object CardMarkedAsKnown : LearnEvent()
    data class DailyGoalCompleted(val learnedCount: Int, val reviewedCount: Int) : LearnEvent()
}