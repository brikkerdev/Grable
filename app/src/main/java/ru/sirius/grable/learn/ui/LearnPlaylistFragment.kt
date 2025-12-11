package ru.sirius.grable.learn.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.sirius.grable.R

class LearnPlaylistFragment: Fragment() {
    private val viewModel: LearnPlaylistViewModel by viewModels {
        LearnPlaylistViewModel.Factory(requireActivity().application, playlistId)
    }
    private  val adapter: LearnPlaylistAdapter by lazy { LearnPlaylistAdapter() }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"

        fun newInstance(id: Long) = LearnPlaylistFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_PLAYLIST_ID, id)
            }
        }
    }

    private val playlistId: Long by lazy {
        arguments?.getLong(ARG_PLAYLIST_ID) ?: 1L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_learn_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        observeViewModel()
    }

    private fun setupRecyclerView(view: View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.word_items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                adapter.submitList(state.words)
            }
        }
    }
}