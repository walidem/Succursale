package cgodin.qc.ca.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cgodin.qc.ca.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    val token: MutableLiveData<String> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()

    fun loginUser(mat: String, mdp: String) {
        viewModelScope.launch {
            try {
                val response = repository.loginUser(mat, mdp)
                if (response.isSuccessful && response.body() != null) {
                    token.postValue(response.body()?.student?.token)
                } else {
                    errorMessage.postValue("Échec de la connexion")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage.postValue(e.message ?: "Erreur inconnue lors de la connexion")
            }
        }
    }
}



