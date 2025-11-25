package ru.sirius.grable.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.sirius.grable.R

class SelectPlaylistFragment: Fragment() {
    val viewModel = SelectPlaylistViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.playlist_items)

        val adapter = SelectPlaylistAdapter()
        recyclerView.adapter = adapter

        viewModel.playlists.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }
}