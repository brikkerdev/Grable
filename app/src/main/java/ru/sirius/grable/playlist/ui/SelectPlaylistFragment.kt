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
import ru.sirius.grable.main.HomeFragment
import ru.sirius.grable.main.PlaylistViewModel
import ru.sirius.grable.main.SelectPlaylistAdapter

class SelectPlaylistFragment : Fragment() {

    // ←←← ИЗМЕНЕНО: правильное создание AndroidViewModel
    private val viewModel: PlaylistViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PlaylistViewModel(requireActivity().application) as T
            }
        }
    }
    // ←←←

    private val adapter: SelectPlaylistAdapter by lazy {
        SelectPlaylistAdapter { playlist ->
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)

        view.findViewById<Button>(R.id.button_back).setOnClickListener {
            (activity as? MainActivity)?.switchFragment(HomeFragment())
        }

        observeViewModel()
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.playlist_items)
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