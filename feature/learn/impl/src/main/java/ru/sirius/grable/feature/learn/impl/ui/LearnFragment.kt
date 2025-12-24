package ru.sirius.grable.feature.learn.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.sirius.grable.feature.learn.api.Constants
import ru.sirius.grable.feature.learn.api.LearnArgs
import ru.sirius.grable.feature.learn.impl.R
import ru.sirius.grable.feature.learn.impl.ui.card.CardFlipPageTransformer
import ru.sirius.grable.feature.learn.impl.ui.card.LearnWordsPagerAdapter

class LearnFragment : Fragment() {

    private var _binding: ru.sirius.grable.feature.learn.impl.databinding.FragmentLearnBinding? = null
    private val binding get() = _binding!!

    private val playlistId: Long by lazy {
        arguments?.getLong(LearnArgs.PLAYLIST_ID, 1L) ?: 1L
    }

    private val viewModel: LearnPlaylistViewModel by viewModel {
        parametersOf(playlistId)
    }

    private val pagerAdapter by lazy {
        LearnWordsPagerAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ru.sirius.grable.feature.learn.impl.databinding.FragmentLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPager()
        renderLoading(viewModel.state.value.isLoading)
        observeState()
    }

    private fun setupPager() {
        binding.cardPager.apply {
            adapter = pagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1
            setPageTransformer(CardFlipPageTransformer().apply {
                isScalable = true
            })
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateProgress(pagerAdapter.itemCount, position)
                }
            })
        }
    }


    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    renderLoading(state.isLoading)
                    if (state.isLoading) return@collect
                    val previousIndex = binding.cardPager.currentItem
                    pagerAdapter.submitWords(state.words)
                    val clampedIndex = previousIndex
                        .coerceIn(0, (pagerAdapter.itemCount - 1).coerceAtLeast(0))
                    binding.cardPager.setCurrentItem(clampedIndex, false)
                    updateProgress(state.words.size, clampedIndex)
                }
            }
        }
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

    private fun updateProgress(total: Int, position: Int = 0) {
        if (total == 0) {
            binding.tvProgress.text = "0/0"
            return
        }
        val current = position + 1
        binding.tvProgress.text = "$current/$total"
    }

    override fun onDestroyView() {
        super.onDestroyView()
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

