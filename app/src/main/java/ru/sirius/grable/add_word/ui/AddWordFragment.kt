package ru.sirius.grable.add_word.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.sirius.grable.add_word.data.Example
import ru.sirius.grable.databinding.FragmentAddWordBinding
import com.example.api.ImageLoader
import org.koin.java.KoinJavaComponent.inject

class AddWordFragment : Fragment() {

    private var _binding: FragmentAddWordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddWordViewModel by viewModels()

    private val adapter by lazy {
        ExampleAdapter { position, example ->
            showEditExampleDialog(position, example)
        }
    }

    private val imageLoader: ImageLoader<android.widget.ImageView> by inject(ImageLoader::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageLoader.load(
            binding.imageExample,
            "https://sun9-48.userapi.com/s/v1/ig2/ImtKfLVzCA809SBorvbrXFcuIEqeuCkgNrllHf1X-AC5lmPKwCKAgnoQADvGDf0D2rhMUKlNqsu0SZCoOlXkYWp1.jpg?quality=95&as=32x26,48x39,72x58,108x87,160x129,240x193,250x201&from=bu&cs=250x0",
            crossfade = true
        )

        binding.rvExample.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExample.adapter = adapter

        binding.btnAddExample.setOnClickListener { showAddExampleDialog() }

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
        val dialog = AddExampleDialogFragment.newInstance()
        dialog.setOnExampleSavedListener(object : AddExampleDialogFragment.OnExampleSavedListener {
            override fun onExampleSaved(example: Example) {
                viewModel.addExample(example)
            }

            override fun onExampleUpdated(example: Example) {
            }
        })
        dialog.show(childFragmentManager, "AddExampleDialog")
    }

    private fun showEditExampleDialog(index: Int, oldExample: Example) {
        val dialog = AddExampleDialogFragment.newInstance(oldExample)
        dialog.setOnExampleSavedListener(object : AddExampleDialogFragment.OnExampleSavedListener {
            override fun onExampleSaved(example: Example) {
            }

            override fun onExampleUpdated(example: Example) {
                val list = viewModel.state.value.examples.toMutableList()
                val updatedIndex = list.indexOfFirst { it.id == oldExample.id }
                if (updatedIndex != -1) {
                    list[updatedIndex] = example
                    viewModel.updateExamples(list)
                }
            }
        })
        dialog.show(childFragmentManager, "EditExampleDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}