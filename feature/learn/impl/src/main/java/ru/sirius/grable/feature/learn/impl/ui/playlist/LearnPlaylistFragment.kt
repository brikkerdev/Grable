package ru.sirius.grable.feature.learn.impl.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.sirius.grable.feature.learn.api.LearnArgs
import ru.sirius.grable.feature.learn.impl.R
import ru.sirius.grable.feature.learn.impl.databinding.FragmentLearnPlaylistBinding
import ru.sirius.grable.feature.learn.impl.ui.LearnPlaylistViewModel

class LearnPlaylistFragment : Fragment() {

    private var _binding: FragmentLearnPlaylistBinding? = null
    private val binding get() = _binding!!

    private val playlistId: Long by lazy {
        arguments?.getLong(LearnArgs.PLAYLIST_ID, 1L) ?: 1L
    }

    private val viewModel: LearnPlaylistViewModel by viewModel {
        parametersOf(playlistId)
    }

    private val adapter: LearnPlaylistAdapter by lazy { LearnPlaylistAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.wordItems.adapter = adapter
        binding.wordItems.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                adapter.submitList(state.words)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(playlistId: Long): LearnPlaylistFragment {
            return LearnPlaylistFragment().apply {
                arguments = Bundle().apply {
                    putLong(LearnArgs.PLAYLIST_ID, playlistId)
                }
            }
        }
    }
}

