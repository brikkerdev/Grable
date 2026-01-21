package ru.sirius.grable.feature.playlist.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.sirius.grable.feature.playlist.impl.R
import ru.sirius.grable.feature.playlist.impl.databinding.BottomSheetCreatePlaylistBinding

class CreatePlaylistBottomSheet(
    private val onCreate: (name: String, description: String?) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener { dismissAllowingStateLoss() }
        binding.btnCreate.setOnClickListener {
            val name = binding.nameEdit.text?.toString()?.trim().orEmpty()
            val description = binding.descEdit.text?.toString()?.trim().orEmpty().ifEmpty { null }

            if (name.isBlank()) {
                binding.nameLayout.error = getString(R.string.playlist_error_empty_name)
                return@setOnClickListener
            }

            binding.nameLayout.error = null
            onCreate(name, description)
            dismissAllowingStateLoss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CreatePlaylistBottomSheet"
    }
}


