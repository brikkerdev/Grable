package ru.sirius.grable.learn.ui

import android.os.Bundle
import android.os.Debug
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.sirius.grable.databinding.FragmentLearnBinding
import ru.sirius.grable.learn.ui.card.CardFlipPageTransformer
import ru.sirius.grable.learn.ui.card.LearnWordsPagerAdapter

class LearnFragment : Fragment() {

    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LearnPlaylistViewModel by viewModels()

    private lateinit var pagerAdapter: LearnWordsPagerAdapter

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
        setupPager()
        setupButtons()
        observeState()
    }

    private fun setupPager() {
        pagerAdapter = LearnWordsPagerAdapter(this)
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

    private fun setupButtons() {
        binding.btnNext.setOnClickListener {
            val next = binding.cardPager.currentItem + 1
            if (next < pagerAdapter.itemCount) {
                binding.cardPager.currentItem = next
            }
        }
        binding.btnPrevious.setOnClickListener {
            val prev = binding.cardPager.currentItem - 1
            if (prev >= 0) {
                binding.cardPager.currentItem = prev
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    val previousIndex = binding.cardPager.currentItem
                    pagerAdapter.submitWords(state.words)
                    val clampedIndex = previousIndex.coerceIn(0, (pagerAdapter.itemCount - 1).coerceAtLeast(0))
                    binding.cardPager.setCurrentItem(clampedIndex, false)
                    updateProgress(state.words.size, clampedIndex)
                }
            }
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
}