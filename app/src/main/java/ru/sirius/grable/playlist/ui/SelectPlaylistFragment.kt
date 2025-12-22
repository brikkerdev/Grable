package ru.sirius.grable.playlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import com.facebook.shimmer.ShimmerFrameLayout
import org.koin.android.ext.android.inject
import ru.sirius.grable.R
import ru.sirius.grable.databinding.FragmentSelectPlaylistBinding
import ru.sirius.grable.main.PlaylistViewModel
import ru.sirius.grable.main.SelectPlaylistAdapter
import ru.sirius.grable.navigation.api.NavigationRouter
import ru.sirius.grable.navigation.api.Screens

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
            val args = Bundle().apply {
                putLong(ru.sirius.grable.feature.learn.api.LearnArgs.PLAYLIST_ID, playlist.id)
            }
            navigationRouter.navigateToScreenByQualifier(
                ru.sirius.grable.feature.learn.api.Constants.LEARN_PLAYLIST_SCREEN,
                args
            )
        }
    }

    private var _binding: FragmentSelectPlaylistBinding? = null
    private val binding get() = _binding!!

    private val navigationRouter: NavigationRouter by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.buttonBack.setOnClickListener {
            navigationRouter.navigateToScreen(Screens.HOME)
        }

        applyLoading(viewModel.state.value.isLoading)
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.playlistItems.adapter = adapter
        binding.playlistItems.layoutManager = GridLayoutManager(requireContext(), 2)
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
        binding.playlistSkeleton.isVisible = isLoading
        binding.contentGroup.isVisible = !isLoading

        if (isLoading) {
            binding.playlistSkeleton.startShimmer()
        } else {
            binding.playlistSkeleton.stopShimmer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.playlistSkeleton.isVisible) {
            binding.playlistSkeleton.startShimmer()
        }
    }

    override fun onPause() {
        binding.playlistSkeleton.stopShimmer()
        super.onPause()
    }

    override fun onDestroyView() {
        binding.playlistSkeleton.stopShimmer()
        _binding = null
        super.onDestroyView()
    }

    private fun navigateToFragmentReflective(
        className: String,
        args: Array<Any?> = emptyArray(),
        parameterTypes: Array<Class<*>> = emptyArray()
    ) {
        try {
            val fragmentClass = Class.forName(className)
            val method = fragmentClass.getMethod("newInstance", *parameterTypes)
            val fragment = method.invoke(null, *args) as? Fragment
            fragment?.let {
                navigationRouter.navigateToFragment(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}