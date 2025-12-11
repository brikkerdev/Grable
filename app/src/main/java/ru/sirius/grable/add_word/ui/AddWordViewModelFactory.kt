package ru.sirius.grable.add_word.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.sirius.grable.common.AppDatabase



class AddWordViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddWordViewModel::class.java)) {
            return AddWordViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}