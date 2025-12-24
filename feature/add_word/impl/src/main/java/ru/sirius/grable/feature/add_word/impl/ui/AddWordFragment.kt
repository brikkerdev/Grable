package ru.sirius.grable.feature.add_word.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.sirius.grable.feature.add_word.impl.R
import ru.sirius.grable.feature.add_word.api.data.Example
import ru.sirius.api.ImageLoader
import org.koin.java.KoinJavaComponent.inject

class AddWordFragment : Fragment() {

    private val viewModel: AddWordViewModel by viewModels()

    private val adapter by lazy {
        ExampleAdapter { position, example ->
            showEditExampleDialog(position, example)
        }
    }

    private var idCounter = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_word, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvExample)
        val btnAddExample = view.findViewById<MaterialButton>(R.id.btnAddExample)
        val img = view.findViewById<ImageView>(R.id.imageExample)

        val imageLoader: ImageLoader<ImageView> by inject(ImageLoader::class.java)
        imageLoader.load(img, "https://sun9-48.userapi.com/s/v1/ig2/ImtKfLVzCA809SBorvbrXFcuIEqeuCkgNrllHf1X-AC5lmPKwCKAgnoQADvGDf0D2rhMUKlNqsu0SZCoOlXkYWp1.jpg?quality=95&as=32x26,48x39,72x58,108x87,160x129,240x193,250x201&from=bu&cs=250x0", crossfade = true)

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
                    viewModel.addExample(Example(idCounter++, en, ru))
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

