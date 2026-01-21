package ru.sirius.grable.feature.learn.impl.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.sirius.grable.feature.learn.impl.R
import ru.sirius.grable.feature.learn.impl.databinding.BottomSheetAddWordBinding
import ru.sirius.grable.feature.playlist.api.Playlist

class AddWordBottomSheet(
    private val playlists: List<Playlist>,
    private val currentPlaylistId: Long,
    private val onSave: (playlistId: Long, original: String, translation: String, transcription: String, example: String?, exampleTranslation: String?) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddWordBinding? = null
    private val binding get() = _binding!!

    private var selectedPlaylistId: Long = currentPlaylistId

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPlaylistSpinner()

        binding.btnCancel.setOnClickListener { dismissAllowingStateLoss() }
        binding.btnSave.setOnClickListener {
            val original = binding.originalEdit.text?.toString()?.trim().orEmpty()
            val translation = binding.translationEdit.text?.toString()?.trim().orEmpty()
            val transcription = binding.transcriptionEdit.text?.toString()?.trim().orEmpty()
            val example = binding.exampleEdit.text?.toString()?.trim()?.ifEmpty { null }
            val exampleTranslation = binding.exampleTranslationEdit.text?.toString()?.trim()?.ifEmpty { null }

            if (original.isBlank() || translation.isBlank()) {
                binding.translationLayout.error = getString(R.string.learn_add_word_error_required)
                return@setOnClickListener
            }

            binding.translationLayout.error = null
            onSave(selectedPlaylistId, original, translation, transcription, example, exampleTranslation)
            dismissAllowingStateLoss()
        }
    }

    private fun setupPlaylistSpinner() {
        val playlistNames = playlists.map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            playlistNames
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.playlistSpinner.adapter = adapter

        val myWordsName = requireContext().getString(R.string.playlist_my_words)
        val currentIndex = playlists.indexOfFirst { it.id == currentPlaylistId }
        val myWordsIndex = playlists.indexOfFirst { 
            it.name == myWordsName || 
            it.name == "Свои слова" || 
            it.name == "My Words" ||
            !it.isSystem
        }.takeIf { it >= 0 } ?: 0

        val selectedIndex = if (currentIndex >= 0) currentIndex else myWordsIndex
        binding.playlistSpinner.setSelection(selectedIndex)
        selectedPlaylistId = playlists[selectedIndex].id

        binding.playlistSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPlaylistId = playlists[position].id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddWordBottomSheet"
    }
}


