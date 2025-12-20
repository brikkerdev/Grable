package ru.sirius.grable.playlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import com.facebook.shimmer.ShimmerFrameLayout
import ru.sirius.grable.MainActivity
import ru.sirius.grable.R
import ru.sirius.grable.learn.ui.LearnPlaylistFragment
import ru.sirius.grable.main.HomeFragment
import ru.sirius.grable.main.PlaylistViewModel
import ru.sirius.grable.main.SelectPlaylistAdapter

class SelectPlaylistFragment : Fragment() {
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

    private val playlistRecyclerView by lazy { requireView().findViewById<RecyclerView>(R.id.playlist_items) }
    private val playlistTitle by lazy { requireView().findViewById<TextView>(R.id.page_name) }
    private val backButton by lazy { requireView().findViewById<Button>(R.id.button_back) }
    private val playlistSkeleton by lazy { requireView().findViewById<ShimmerFrameLayout>(R.id.playlist_skeleton) }
    private val contentGroup by lazy { requireView().findViewById<LinearLayout>(R.id.content_group) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        backButton.setOnClickListener {
            (activity as? MainActivity)?.switchFragment(HomeFragment())
        }

        applyLoading(viewModel.state.value.isLoading)
        observeViewModel()
    }

    private fun setupRecyclerView() {
        playlistRecyclerView.adapter = adapter
        playlistRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                applyLoading(state.isLoading)
                if (!state.isLoading) {
                    adapter.submitList(state.playlists)
                }
            }
        }
    }

    private fun applyLoading(isLoading: Boolean) {
        playlistSkeleton.isVisible = isLoading
        contentGroup.isVisible = !isLoading

        if (isLoading) {
            playlistSkeleton.startShimmer()
        } else {
            playlistSkeleton.stopShimmer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (playlistSkeleton.isVisible) {
            playlistSkeleton.startShimmer()
        }
    }

    override fun onPause() {
        playlistSkeleton.stopShimmer()
        super.onPause()
    }
}