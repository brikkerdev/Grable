package ru.sirius.grable.playlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.sirius.grable.MainActivity
import ru.sirius.grable.R
import ru.sirius.grable.databinding.FragmentSelectPlaylistBinding
import ru.sirius.grable.learn.ui.LearnPlaylistFragment
import ru.sirius.grable.main.HomeFragment
import ru.sirius.grable.main.PlaylistViewModel
import ru.sirius.grable.main.SelectPlaylistAdapter

class SelectPlaylistFragment : Fragment() {
    private var _binding: FragmentSelectPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PlaylistViewModel(requireActivity().application) as T
            }
        }
    }

    private val adapter: SelectPlaylistAdapter by lazy {
        SelectPlaylistAdapter { playlist ->
            (activity as? MainActivity)?.switchFragment(
                LearnPlaylistFragment.newInstance(playlist.id)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)

        binding.buttonBack.setOnClickListener {
            (activity as? MainActivity)?.switchFragment(HomeFragment())
        }

        observeViewModel()
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = binding.playlistItems
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                adapter.submitList(state.playlists)
            }
        }
    }
}