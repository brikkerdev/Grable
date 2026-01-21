package ru.sirius.grable.feature.add_word.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.sirius.grable.feature.add_word.impl.R
import ru.sirius.grable.feature.add_word.api.data.Example
import ru.sirius.grable.feature.add_word.impl.databinding.FragmentAddWordBinding
import java.util.Random

class AddWordFragment : Fragment() {

    private var _binding: FragmentAddWordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddWordViewModel by viewModel()
    private val random = Random()
    private var selectedPlaylistId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPlaylistSpinner()
        setupSaveButton()
    }

    private fun setupPlaylistSpinner() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playlists.collectLatest { playlists ->
                if (playlists.isNotEmpty()) {
                    val playlistNames = playlists.map { it.name }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        playlistNames
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                    binding.playlistSpinner.adapter = adapter
                    // TODO
                    // val myWordsName = requireContext().getString(ru.sirius.grable.feature.learn.impl.R.string.playlist_my_words)
                    val myWordsIndex = playlists.indexOfFirst {
                        it.name == "Свои слова" ||
                        it.name == "My Words" ||
                        !it.isSystem
                    }.takeIf { it >= 0 } ?: 0

                    binding.playlistSpinner.setSelection(myWordsIndex)
                    selectedPlaylistId = playlists[myWordsIndex].id

                    binding.playlistSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            selectedPlaylistId = playlists[position].id
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }
            }
        }
    }


    private fun setupSaveButton() {
        binding.btnAdd.setOnClickListener {
            val original = binding.inputWord.text?.toString()?.trim().orEmpty()
            val translation = binding.inputTranslation.text?.toString()?.trim().orEmpty()
            val transcription = binding.inputTranscription.text?.toString()?.trim().orEmpty()
            val exampleText = binding.inputExample.text?.toString()?.trim().orEmpty()
            val exampleTranslation = binding.inputExampleTranslation.text?.toString()?.trim().orEmpty()

            if (original.isBlank() || translation.isBlank()) {
                binding.inputLayoutTranslation.error = getString(R.string.add_word_error_required)
                return@setOnClickListener
            }

            if (selectedPlaylistId < 0) {
                Toast.makeText(requireContext(), getString(R.string.add_word_error_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.inputLayoutTranslation.error = null

            val examples = if (exampleText.isNotBlank()) {
                listOf(Example(random.nextInt(Int.MAX_VALUE), exampleText, exampleTranslation))
            } else {
                emptyList()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val success = viewModel.saveWord(
                    playlistId = selectedPlaylistId,
                    original = original,
                    transcription = transcription,
                    translation = translation,
                    examples = examples
                )
                if (success) {
                    Toast.makeText(requireContext(), getString(R.string.add_word_success), Toast.LENGTH_SHORT).show()
                    clearForm()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.add_word_error_required), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun clearForm() {
        binding.inputWord.text?.clear()
        binding.inputTranscription.text?.clear()
        binding.inputTranslation.text?.clear()
        binding.inputExample.text?.clear()
        binding.inputExampleTranslation.text?.clear()
        viewModel.clear()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


