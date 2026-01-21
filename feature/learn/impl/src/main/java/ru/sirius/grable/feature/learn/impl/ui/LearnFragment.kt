package ru.sirius.grable.feature.learn.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.Duration
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import ru.sirius.fsrs.Rating
import ru.sirius.grable.feature.learn.api.LearnArgs
import ru.sirius.grable.feature.learn.impl.databinding.FragmentLearnBinding
import ru.sirius.grable.feature.learn.impl.ui.card.CardStackAdapter
import ru.sirius.grable.feature.settings.api.ISettingsService

class LearnFragment : Fragment(), CardStackListener {

    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    private val playlistIds: List<Long> by lazy {
        val savedIds = settingsService.getSelectedPlaylistIds()
        if (savedIds.isNotEmpty()) {
            savedIds
        } else {
            val id = arguments?.getLong(LearnArgs.PLAYLIST_ID, 1L) ?: 1L
            listOf(id)
        }
    }

    private val viewModel: LearnViewModel by viewModel()
    private val settingsService: ISettingsService by inject()

    private val cardStackAdapter by lazy { CardStackAdapter() }
    private var layoutManager: CardStackLayoutManager? = null
    
    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences("app_preferences", android.content.Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCardStack()
        observeState()
        
        val newWordsPerDay = settingsService.getNewWordsPerDay()
        
        val today = getTodayDateKey()
        val savedDate = sharedPreferences.getString("learn_progress_date", "")
        val savedLearnedCount = sharedPreferences.getInt("learn_progress_learned", 0)
        val savedReviewedCount = sharedPreferences.getInt("learn_progress_reviewed", 0)
        
        val initialLearnedCount = if (savedDate == today) savedLearnedCount else 0
        val initialReviewedCount = if (savedDate == today) savedReviewedCount else 0
        
        if (initialLearnedCount >= newWordsPerDay) {
            showCompletionScreen(initialLearnedCount, initialReviewedCount)
        } else {
            viewModel.startSession(playlistIds, newWordsPerDay, initialLearnedCount, initialReviewedCount)
        }
    }

    private fun getTodayDateKey(): String {
        val calendar = java.util.Calendar.getInstance()
        return "${calendar.get(java.util.Calendar.YEAR)}-${calendar.get(java.util.Calendar.MONTH) + 1}-${calendar.get(java.util.Calendar.DAY_OF_MONTH)}"
    }

    private fun saveTodayProgress(learnedCount: Int, reviewedCount: Int) {
        sharedPreferences.edit().apply {
            putString("learn_progress_date", getTodayDateKey())
            putInt("learn_progress_learned", learnedCount)
            putInt("learn_progress_reviewed", reviewedCount)
            apply()
        }
    }

    private fun setupCardStack() {
        layoutManager = CardStackLayoutManager(requireContext(), this).apply {
            setStackFrom(StackFrom.Top)
            setVisibleCount(3)
            setTranslationInterval(8.0f)
            setScaleInterval(0.95f)
            setMaxDegree(20.0f)
            setDirections(Direction.FREEDOM)
            setCanScrollHorizontal(true)
            setCanScrollVertical(true)
        }
        
        binding.cardStackView.layoutManager = layoutManager
        binding.cardStackView.adapter = cardStackAdapter

        cardStackAdapter.onCardFlip = { position ->
        }

        cardStackAdapter.onMarkAsKnown = { word ->
            viewModel.onMarkAsKnown(word)
            
            lifecycleScope.launch {
                kotlinx.coroutines.delay(100)
                val state = viewModel.uiState.value
                if (state is LearnUiState.Content) {
                    saveTodayProgress(state.learnedProgress, 0)
                }
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is LearnUiState.Loading -> {
                                renderLoading(true)
                            }
                            is LearnUiState.Empty -> {
                                renderLoading(false)
                                binding.emptyView.isVisible = true
                            }
                            is LearnUiState.Content -> {
                                renderLoading(false)
                                binding.emptyView.isVisible = false
                                cardStackAdapter.submitList(state.cards)
                                updateProgress(state.learnedProgress, state.targetProgress)
                            }
                            is LearnUiState.Error -> {
                                renderLoading(false)
                            }
                        }
                    }
                }

                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            is LearnEvent.DailyGoalCompleted -> {
                                saveTodayProgress(event.learnedCount, event.reviewedCount)
                                showCompletionScreen(event.learnedCount, event.reviewedCount)
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun showCompletionScreen(learnedCount: Int, reviewedCount: Int) {
        val fragment = LearnCompletionFragment.newInstance(learnedCount, reviewedCount)
        parentFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun renderLoading(isLoading: Boolean) {
        binding.learnSkeleton.isVisible = isLoading
        binding.contentGroup.isVisible = !isLoading
        if (isLoading) {
            binding.learnSkeleton.startShimmer()
        } else {
            binding.learnSkeleton.stopShimmer()
        }
    }

    private fun updateProgress(learned: Int, target: Int) {
        binding.progressBar.max = target
        binding.progressBar.progress = learned
    }

    // CardStackListener methods
    override fun onCardDragging(direction: Direction, ratio: Float) {
        val currentPosition = layoutManager?.topPosition ?: return
        if (currentPosition >= cardStackAdapter.itemCount) return

        val viewHolder = binding.cardStackView.findViewHolderForAdapterPosition(currentPosition)
        if (viewHolder is CardStackAdapter.CardViewHolder) {
            when (direction) {
                Direction.Left -> {
                    viewHolder.overlayLeft.alpha = ratio
                    viewHolder.overlayRight.alpha = 0f
                    viewHolder.overlayUp.alpha = 0f
                }
                Direction.Right -> {
                    viewHolder.overlayRight.alpha = ratio
                    viewHolder.overlayLeft.alpha = 0f
                    viewHolder.overlayUp.alpha = 0f
                }
                Direction.Top -> {
                    viewHolder.overlayUp.alpha = ratio
                    viewHolder.overlayLeft.alpha = 0f
                    viewHolder.overlayRight.alpha = 0f
                }
                else -> {
                    viewHolder.overlayLeft.alpha = 0f
                    viewHolder.overlayRight.alpha = 0f
                    viewHolder.overlayUp.alpha = 0f
                }
            }
        }
    }

    override fun onCardSwiped(direction: Direction) {
        val state = viewModel.uiState.value
        if (state !is LearnUiState.Content) return
        
        val word = state.cards.firstOrNull() ?: return

        val rating = when (direction) {
            Direction.Left -> Rating.Again
            Direction.Right -> Rating.Good
            Direction.Top -> Rating.Hard
            else -> return
        }

        viewModel.onSwipe(word, rating)

        lifecycleScope.launch {
            kotlinx.coroutines.delay(100)
            val currentState = viewModel.uiState.value
            if (currentState is LearnUiState.Content) {
                saveTodayProgress(currentState.learnedProgress, 0)
            }
        }
    }

    override fun onCardRewound() {
    }

    override fun onCardCanceled() {
        val currentPosition = layoutManager?.topPosition ?: return
        if (currentPosition >= cardStackAdapter.itemCount) return

        val viewHolder = binding.cardStackView.findViewHolderForAdapterPosition(currentPosition)
        if (viewHolder is CardStackAdapter.CardViewHolder) {
            viewHolder.overlayLeft.alpha = 0f
            viewHolder.overlayRight.alpha = 0f
            viewHolder.overlayUp.alpha = 0f
        }
    }

    override fun onCardAppeared(view: View, position: Int) {
        val viewHolder = binding.cardStackView.findViewHolderForAdapterPosition(position)
        if (viewHolder is CardStackAdapter.CardViewHolder) {
            viewHolder.overlayLeft.alpha = 0f
            viewHolder.overlayRight.alpha = 0f
            viewHolder.overlayUp.alpha = 0f
        }
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val viewHolder = binding.cardStackView.findViewHolderForAdapterPosition(position)
        if (viewHolder is CardStackAdapter.CardViewHolder) {
            viewHolder.overlayLeft.alpha = 0f
            viewHolder.overlayRight.alpha = 0f
            viewHolder.overlayUp.alpha = 0f
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutManager = null
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null && binding.learnSkeleton.isVisible) {
            binding.learnSkeleton.startShimmer()
        }
    }

    override fun onPause() {
        if (_binding != null) {
            binding.learnSkeleton.stopShimmer()
        }
        super.onPause()
    }

    companion object {
        fun newInstance(playlistId: Long = 1L): LearnFragment {
            return LearnFragment().apply {
                arguments = Bundle().apply {
                    putLong(LearnArgs.PLAYLIST_ID, playlistId)
                }
            }
        }
    }
}

