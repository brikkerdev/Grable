package ru.sirius.grable.add_word

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import ru.sirius.grable.R
import kotlinx.coroutines.flow.collect

class AddWordFragment : Fragment() {

    private val viewModel: AddWordViewModel by viewModels()
    private lateinit var adapter: ExamplesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_word, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvExamples)
        val btnAddExample = view.findViewById<MaterialButton>(R.id.btnAddExample)

        adapter = ExamplesAdapter(emptyList()) { position, text ->
            showEditExampleDialog(position, text)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        lifecycleScope.launchWhenStarted {
            viewModel.examples.collect { list ->
                adapter.updateData(list)
            }
        }

        btnAddExample.setOnClickListener { showAddExampleDialog() }

        viewModel.generateFakeExamples(3)
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
                if (en.isNotEmpty()) viewModel.addExample("$en — $ru")
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showEditExampleDialog(index: Int, oldText: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_example, null)
        val inputEn = dialogView.findViewById<TextInputEditText>(R.id.inputExampleEn)
        val inputRu = dialogView.findViewById<TextInputEditText>(R.id.inputExampleRu)

        val parts = oldText.split("—")
        inputEn.setText(parts.getOrNull(0)?.trim())
        inputRu.setText(parts.getOrNull(1)?.trim())

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Редактировать пример")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { dialog, _ ->
                val en = inputEn.text.toString().trim()
                val ru = inputRu.text.toString().trim()
                if (en.isNotEmpty()) {
                    val list = viewModel.examples.value.toMutableList()
                    list[index] = "$en — $ru"
                    viewModel.updateExamples(list)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
