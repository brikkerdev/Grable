package ru.sirius.grable.feature.playlist.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.sirius.grable.feature.playlist.impl.databinding.FragmentSelectPlaylistBinding
import ru.sirius.grable.core.navigation.api.NavigationRouter
import ru.sirius.grable.core.navigation.api.Screens


class SelectPlaylistFragment : Fragment() {
    private val viewModel: SelectPlaylistViewModel by viewModels()

    private val adapter: SelectPlaylistAdapter by lazy {
        SelectPlaylistAdapter(
            onItemClick = { playlist ->
                if (!viewModel.state.value.isEditMode) {
                    val args = Bundle().apply {
                        putLong(ru.sirius.grable.feature.learn.api.LearnArgs.PLAYLIST_ID, playlist.id)
                    }
                    navigationRouter.navigateToScreenByQualifier(
                        ru.sirius.grable.feature.learn.api.Constants.LEARN_PLAYLIST_SCREEN,
                        args
                    )
                }
            },
            onCheckboxClick = { playlistId ->
                viewModel.togglePlaylistSelection(playlistId)
            }
        )
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

        binding.buttonAddCategory.setOnClickListener {
            CreatePlaylistBottomSheet { name, description ->
                viewModel.createPlaylist(name, description)
            }.show(childFragmentManager, CreatePlaylistBottomSheet.TAG)
        }

        binding.buttonEdit.setOnClickListener {
            viewModel.toggleEditMode()
        }

        binding.buttonApply.setOnClickListener {
            viewModel.applySelectedPlaylists()
        }

        applyLoading(viewModel.state.value.isLoading)
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.playlistItems.adapter = adapter
        binding.playlistItems.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                applyLoading(state.isLoading)
                if (!state.isLoading) {
                    adapter.isEditMode = state.isEditMode
                    adapter.selectedPlaylistIds = state.selectedPlaylistIds
                    adapter.submitList(state.playlists)
                }
                updateEditModeUI(state.isEditMode)
            }
        }
    }

    private fun updateEditModeUI(isEditMode: Boolean) {
        binding.buttonBack.isVisible = !isEditMode
        binding.buttonAddCategory.isVisible = !isEditMode
        binding.buttonEdit.visibility = if (isEditMode) View.GONE else View.VISIBLE
        binding.buttonApply.visibility = if (isEditMode) View.VISIBLE else View.GONE
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