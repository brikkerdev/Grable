package ru.sirius.grable.feature.learn.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import ru.sirius.grable.core.navigation.api.NavigationRouter
import ru.sirius.grable.core.navigation.api.Screens
import ru.sirius.grable.feature.learn.impl.databinding.FragmentLearnCompletionBinding

class LearnCompletionFragment : Fragment() {

    private var _binding: FragmentLearnCompletionBinding? = null
    private val binding get() = _binding!!

    private val navigationRouter: NavigationRouter by inject()

    private val learnedCount: Int by lazy {
        arguments?.getInt(ARG_LEARNED_COUNT, 0) ?: 0
    }

    private val reviewedCount: Int by lazy {
        arguments?.getInt(ARG_REVIEWED_COUNT, 0) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnCompletionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.statsLearnedCount.text = learnedCount.toString()
        binding.statsReviewedCount.text = reviewedCount.toString()

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
            navigationRouter.navigateToScreen(Screens.HOME)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_LEARNED_COUNT = "learned_count"
        private const val ARG_REVIEWED_COUNT = "reviewed_count"

        fun newInstance(learnedCount: Int, reviewedCount: Int): LearnCompletionFragment {
            return LearnCompletionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_LEARNED_COUNT, learnedCount)
                    putInt(ARG_REVIEWED_COUNT, reviewedCount)
                }
            }
        }
    }
}

