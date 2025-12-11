package ru.sirius.grable.learn.ui.card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.sirius.grable.databinding.WordCardBinding
import ru.sirius.grable.learn.ui.Word

class WordFragment : Fragment() {

    private var _binding: WordCardBinding? = null
    private val binding get() = _binding!!

    private var showingFront = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WordCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val word = requireArguments().getSerializable(ARG_WORD) as Word
        bindWord(word)
        binding.root.setOnClickListener { flipCard() }
    }

    private fun bindWord(word: Word) {
        showingFront = true
        binding.frontGroup.visibility = View.VISIBLE
        binding.backGroup.visibility = View.GONE
        binding.frontGroup.rotationY = 0f
        binding.backGroup.rotationY = 0f

        binding.frontWord.text = word.original
        binding.frontTranscription.text = word.transcription
        binding.backTranslation.text = word.translation
        binding.backExample.text = word.example
    }

    private fun flipCard() {
        val front = binding.frontGroup
        val back = binding.backGroup
        val visible = if (showingFront) front else back
        val hidden = if (showingFront) back else front

        visible.animate()
            .rotationY(90f)
            .setDuration(150)
            .withEndAction {
                visible.visibility = View.GONE
                visible.rotationY = 0f
                hidden.visibility = View.VISIBLE
                hidden.rotationY = -90f
                hidden.animate()
                    .rotationY(0f)
                    .setDuration(150)
                    .start()
            }
            .start()
        showingFront = !showingFront
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_WORD = "arg_word"

        fun newInstance(word: Word): WordFragment {
            return WordFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_WORD, word)
                }
            }
        }
    }
}