package ru.sirius.grable.add_word.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import android.text.Editable
import android.text.TextWatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.sirius.grable.R
import ru.sirius.grable.common.load
import ru.sirius.grable.common.AppDatabase
import kotlinx.coroutines.flow.StateFlow
import ru.sirius.grable.common.ExampleEntity

class AddWordFragment : Fragment() {

    private val viewModel: AddWordViewModel by viewModels {
        AddWordViewModelFactory(
            AppDatabase.getDatabase(requireContext())
        )
    }

    private val adapter by lazy {
        ExampleAdapter { position, example ->
            showEditExampleDialog(position, example)
        }
    }

    private lateinit var inputWord: TextInputEditText
    private lateinit var inputTranscription: TextInputEditText
    private lateinit var inputTranslation: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_word, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvExample)
        val btnAddExample = view.findViewById<MaterialButton>(R.id.btnAddExample)
        //val img = view.findViewById<ImageView>(R.id.imageExample)

        //img.load("https://sun9-48.userapi.com/s/v1/ig2/ImtKfLVzCA809SBorvbrXFcuIEqeuCkgNrllHf1X-AC5lmPKwCKAgnoQADvGDf0D2rhMUKlNqsu0SZCoOlXkYWp1.jpg?quality=95&as=32x26,48x39,72x58,108x87,160x129,240x193,250x201&from=bu&cs=250x0", crossfade = true)

        inputWord = view.findViewById<TextInputEditText>(R.id.inputWord)
        inputTranscription = view.findViewById<TextInputEditText>(R.id.inputTranscription)
        inputTranslation = view.findViewById<TextInputEditText>(R.id.inputTranslation)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnAdd)

        inputWord.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setWord(s.toString())
            }
        })

        inputTranscription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setTranscription(s.toString())
            }
        })

        inputTranslation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setTranslation(s.toString())
            }
        })

        btnSave.setOnClickListener {
            viewModel.save()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAddExample.setOnClickListener { showAddExampleDialog() }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                adapter.submitList(state.examples)
                if (inputWord.text.toString() != state.word) {
                    inputWord.setText(state.word)
                }
                if (inputTranscription.text.toString() != state.transcription) {
                    inputTranscription.setText(state.transcription)
                }
                if (inputTranslation.text.toString() != state.translation) {
                    inputTranslation.setText(state.translation)
                }
            }
        }
    }

    private fun showAddExampleDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_example, null)
        val inputEn = dialogView.findViewById<TextInputEditText>(R.id.inputExampleEn)
        val inputRu = dialogView.findViewById<TextInputEditText>(R.id.inputExampleRu)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Добавить пример")
            .setView(dialogView)
            .setPositiveButton("Добавить") { dialog, _ ->
                val en = inputEn.text.toString().trim()
                val ru = inputRu.text.toString().trim()
                if (en.isNotEmpty()) {
                    val newId = viewModel.state.value.examples.size + 1
                    viewModel.addExample(Example(newId, en, ru))
                }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showEditExampleDialog(index: Int, oldExample: Example) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_example, null)
        val inputEn = dialogView.findViewById<TextInputEditText>(R.id.inputExampleEn)
        val inputRu = dialogView.findViewById<TextInputEditText>(R.id.inputExampleRu)

        inputEn.setText(oldExample.english)
        inputRu.setText(oldExample.russian)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Редактировать пример")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { dialog, _ ->
                val en = inputEn.text.toString().trim()
                val ru = inputRu.text.toString().trim()
                if (en.isNotEmpty()) {
                    val list = viewModel.state.value.examples.toMutableList()
                    list[index] = Example(oldExample.id, en, ru)
                    viewModel.updateExamples(list)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}