package ru.sirius.grable.learn.ui

import android.os.Bundle
import android.os.Debug
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import coil3.util.DebugLogger
import ru.sirius.embedded.TTSImpl
import ru.sirius.grable.MainActivity
import ru.sirius.grable.R
import ru.sirius.grable.main.HomeFragment

class LearnFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_learn, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tts = TTSImpl(view.context)

        val text = view.findViewById<TextView>(R.id.tvForeignWord).text as String

        view.findViewById<ImageButton>(R.id.btnAudio).setOnClickListener {
            tts.play(text)
        }

    }
}