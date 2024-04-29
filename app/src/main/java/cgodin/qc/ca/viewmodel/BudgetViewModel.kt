package cgodin.qc.ca.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cgodin.qc.ca.model.SuccursaleBudgetResponse
import cgodin.qc.ca.repository.SuccursaleRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BudgetViewModel(private val repository: SuccursaleRepository) : ViewModel() {
    val succursaleBudgetResponse: MutableLiveData<SuccursaleBudgetResponse> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()

    fun fetchSuccursaleBudget(aut: String, ville: String) {
        viewModelScope.launch {
            try {
                val response = repository.getSuccursaleBudget(aut, ville)
                if (response.isSuccessful && response.body() != null) {
                    succursaleBudgetResponse.postValue(response.body())
                } else {
                    errorMessage.postValue("Error: ${response.code()} ${response.message()}")
                }
            } catch (t: Throwable) {
                errorMessage.postValue("Failure: ${t.message}")
            }
        }
    }
}

