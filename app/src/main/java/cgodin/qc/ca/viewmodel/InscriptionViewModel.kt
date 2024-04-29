package cgodin.qc.ca.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cgodin.qc.ca.model.StudentRegistrationResponse
import cgodin.qc.ca.repository.InscriptionRepository
import kotlinx.coroutines.launch

class InscriptionViewModel(private val repository: InscriptionRepository) : ViewModel() {
    val inscriptionResponse: MutableLiveData<StudentRegistrationResponse> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()

    fun inscrire(mat: String, motDePasse: String, nom: String, prenom: String) {
        viewModelScope.launch {
            try {
                val response = repository.inscrire(mat, motDePasse, nom, prenom)
                if (response.isSuccessful && response.body() != null) {
                    inscriptionResponse.postValue(response.body())
                } else {
                    val error = response.errorBody()?.string() ?: "Échec de l'inscription"
                    errorMessage.postValue(error)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage.postValue(e.message ?: "Erreur inconnue lors de l'inscription")
            }
        }
    }
}
