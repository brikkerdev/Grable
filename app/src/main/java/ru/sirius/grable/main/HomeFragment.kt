package ru.sirius.grable.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import ru.sirius.grable.MainActivity
import ru.sirius.grable.R
import ru.sirius.grable.learn.LearnFragment
import ru.sirius.grable.playlist.SelectPlaylistFragment

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_repeat)?.setOnClickListener {
            (activity as? MainActivity)?.switchFragment(LearnFragment())
        }

        view.findViewById<Button>(R.id.button_collection)?.setOnClickListener {
        }

        view.findViewById<Button>(R.id.button_categories)?.setOnClickListener {
            (activity as? MainActivity)?.switchFragment(SelectPlaylistFragment())
        }
    }
}