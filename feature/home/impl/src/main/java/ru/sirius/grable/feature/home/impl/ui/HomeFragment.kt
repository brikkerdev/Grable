package ru.sirius.grable.feature.home.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.sirius.grable.feature.home.impl.R
import ru.sirius.grable.feature.home.impl.databinding.FragmentMainScreenBinding
import ru.sirius.grable.core.navigation.api.NavigationRouter
import ru.sirius.grable.core.navigation.api.Screens
import ru.sirius.grable.feature.settings.api.ISettingsService

class HomeFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    private val navigationRouter: NavigationRouter by inject()
    private val viewModel: HomeViewModel by viewModel()
    private val settingsService: ISettingsService by inject()
    
    private val actionsAdapter by lazy {
        HomeActionsAdapter { action ->
            val state = viewModel.uiState.value
            val newWordsPerDay = settingsService.getNewWordsPerDay()
            val isDailyGoalCompleted = state.learnedNewWordsToday >= newWordsPerDay

            if (action.screen == Screens.LEARN && isDailyGoalCompleted) {
                return@HomeActionsAdapter
            }

            navigationRouter.navigateToScreen(action.screen)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeState()
    }

    private fun setupRecyclerView() {
        binding.actionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = actionsAdapter
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateActions(state)
                }
            }
        }
    }

    private fun updateActions(state: HomeUiState) {
        val newWordsPerDay = settingsService.getNewWordsPerDay()
        val isDailyGoalCompleted = state.learnedNewWordsToday >= newWordsPerDay

        val actions = buildList {
            add(
                HomeAction(
                    titleRes = if (isDailyGoalCompleted) {
                        R.string.action_goal_completed_title
                    } else {
                        R.string.action_repeat_title
                    },
                    descriptionRes = if (isDailyGoalCompleted) {
                        R.string.action_goal_completed_desc
                    } else {
                        R.string.action_repeat_desc
                    },
                    buttonTextRes = if (isDailyGoalCompleted) {
                        R.string.learn_daily_goal_completed
                    } else {
                        R.string.action_repeat_cta
                    },
                    screen = Screens.LEARN,
                    badge = when {
                        isDailyGoalCompleted -> null
                        state.dueWordsCount > 0 -> "У вас ${state.dueWordsCount} слов для повторения"
                        else -> null
                    }
                )
            )
            add(
                HomeAction(
                    titleRes = R.string.action_collection_title,
                    descriptionRes = R.string.action_collection_desc,
                    buttonTextRes = R.string.action_collection_cta,
                    screen = Screens.SELECT_PLAYLIST
                )
            )
            add(
                HomeAction(
                    titleRes = R.string.action_add_word_title,
                    descriptionRes = R.string.action_add_word_desc,
                    buttonTextRes = R.string.action_add_word_cta,
                    screen = Screens.ADD_WORD
                )
            )
        }
        actionsAdapter.submitList(actions)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
