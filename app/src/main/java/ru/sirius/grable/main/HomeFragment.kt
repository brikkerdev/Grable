package ru.sirius.grable.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.sirius.grable.R
import java.util.zip.Inflater

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPlaylists()
        val grid = view.findViewById<GridView>(R.id.categories_grid);
        viewModel.state.value.playlists.forEach { playlist ->
            val item = layoutInflater.inflate(R.layout.item_playlist_preview, grid, false)
            (item as TextView).text = playlist.name
        }
    }
}