package ru.sirius.grable.add_word.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import ru.sirius.grable.R
import ru.sirius.grable.add_word.data.Example
import java.util.Random

class AddExampleDialogFragment : DialogFragment() {

    private var example: Example? = null
    private var listener: OnExampleSavedListener? = null
    private val random = Random()

    interface OnExampleSavedListener {
        fun onExampleSaved(example: Example)
        fun onExampleUpdated(example: Example)
    }

    companion object {
        private const val ARG_EXAMPLE = "example"
        private const val ARG_IS_EDIT = "is_edit"

        fun newInstance(example: Example? = null): AddExampleDialogFragment {
            return AddExampleDialogFragment().apply {
                arguments = Bundle().apply {
                    example?.let {
                        putParcelable(ARG_EXAMPLE, it)
                        putBoolean(ARG_IS_EDIT, true)
                    } ?: putBoolean(ARG_IS_EDIT, false)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            example = it.getParcelable(ARG_EXAMPLE)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_example, null)
        
        val inputEn = dialogView.findViewById<TextInputEditText>(R.id.inputExampleEn)
        val inputRu = dialogView.findViewById<TextInputEditText>(R.id.inputExampleRu)

        // Заполняем поля, если редактируем
        example?.let {
            inputEn.setText(it.english)
            inputRu.setText(it.russian)
        }

        val isEdit = arguments?.getBoolean(ARG_IS_EDIT, false) ?: false
        val title = if (isEdit) "Редактировать пример" else "Добавить пример"
        val positiveButtonText = if (isEdit) "Сохранить" else "Добавить"

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton(positiveButtonText) { _, _ ->
                val en = inputEn.text.toString().trim()
                val ru = inputRu.text.toString().trim()
                if (en.isNotEmpty()) {
                    val resultExample = if (isEdit && example != null) {
                        Example(example!!.id, en, ru)
                    } else {
                        Example(random.nextInt(Int.MAX_VALUE), en, ru)
                    }
                    
                    if (isEdit) {
                        listener?.onExampleUpdated(resultExample)
                    } else {
                        listener?.onExampleSaved(resultExample)
                    }
                }
            }
            .setNegativeButton("Отмена") { _, _ -> }
            .create()
            .apply {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            }
    }

    fun setOnExampleSavedListener(listener: OnExampleSavedListener) {
        this.listener = listener
    }
}

