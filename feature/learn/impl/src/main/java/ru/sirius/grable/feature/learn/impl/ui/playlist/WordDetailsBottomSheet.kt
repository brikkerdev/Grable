package ru.sirius.grable.feature.learn.impl.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.sirius.grable.feature.learn.impl.databinding.BottomSheetWordDetailsBinding
import ru.sirius.grable.feature.learn.impl.domain.Word

class WordDetailsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetWordDetailsBinding? = null
    private val binding get() = _binding!!

    private val word: Word by lazy {
        arguments?.getSerializable(ARG_WORD) as? Word ?: throw IllegalArgumentException("Word is required")
    }

    var onMarkAsKnown: ((Word) -> Unit)? = null
    var onDelete: ((Word) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetWordDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.wordOriginal.text = word.original
        binding.wordTranscription.text = word.transcription
        binding.wordTranslation.text = word.translation

        val examplesText = word.examples.joinToString("\n\n") { example ->
            example.text
        }
        binding.wordExamples.text = examplesText.ifEmpty { "-" }

        val isLearned = word.fsrsData.reviewCount > 0 && word.fsrsData.phase == ru.sirius.fsrs.CardPhase.Review.value
        binding.btnMarkAsKnown.visibility = if (isLearned) View.GONE else View.VISIBLE

        binding.btnMarkAsKnown.setOnClickListener {
            onMarkAsKnown?.invoke(word)
            dismiss()
        }

        binding.btnDelete.setOnClickListener {
            onDelete?.invoke(word)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_WORD = "word"

        fun newInstance(word: Word): WordDetailsBottomSheet {
            return WordDetailsBottomSheet().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_WORD, word)
                }
            }
        }
    }
}

