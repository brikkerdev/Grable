package ru.sirius.grable.add_word.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.sirius.grable.R
import ru.sirius.grable.add_word.data.Example

class AddWordFragment : Fragment() {

    private val viewModel: AddWordViewModel by viewModels()

    private val adapter by lazy {
        ExamplesAdapter { position, example ->
            showEditExampleDialog(position, example)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_word, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvExample)
        val btnAddExample = view.findViewById<MaterialButton>(R.id.btnAddExample)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAddExample.setOnClickListener { showAddExampleDialog() }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                adapter.submitList(state.examples)
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
                    viewModel.addExample(Example(id,en, ru))
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
                    list[index] = Example(id, en, ru)
                    viewModel.updateExamples(list)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}