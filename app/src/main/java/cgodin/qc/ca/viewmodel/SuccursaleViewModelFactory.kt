package cgodin.qc.ca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cgodin.qc.ca.repository.SuccursaleRepository

class SuccursaleViewModelFactory(private val repository: SuccursaleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SuccursaleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SuccursaleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}