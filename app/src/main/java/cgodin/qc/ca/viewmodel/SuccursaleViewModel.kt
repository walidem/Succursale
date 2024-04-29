package cgodin.qc.ca.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cgodin.qc.ca.model.Succursale
import cgodin.qc.ca.model.SuccursaleBudgetResponse
import cgodin.qc.ca.repository.SuccursaleRepository
import kotlinx.coroutines.launch

class SuccursaleViewModel(private val repository: SuccursaleRepository) : ViewModel() {
    val succursales: MutableLiveData<List<Succursale>> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val succursaleBudgetResponse: MutableLiveData<SuccursaleBudgetResponse> = MutableLiveData()
    val allSuccursalesDeleted: MutableLiveData<Boolean> = MutableLiveData()
    val favoris: LiveData<List<Succursale>> = repository.getFavoris()
    val successMessage: MutableLiveData<String> = MutableLiveData()


    fun updateFavoris(succursale: Succursale) {
        viewModelScope.launch {
            repository.updateFavoris(succursale)
        }
    }

    private fun fetchSuccursales(aut: String) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getSuccursaleList(aut)
                if (response.isSuccessful && response.body() != null) {
                    succursales.postValue(response.body()?.succursales ?: listOf())
                } else {
                    errorMessage.postValue("Erreur de chargement des succursales")
                }
            } catch (e: Exception) {
                errorMessage.postValue(e.message ?: "Erreur inconnue")
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun ajouterSuccursale(aut: String, ville: String, budget: String) {
        viewModelScope.launch {
            try {
                val response = repository.addSuccursale(aut, ville, budget)
                if (response.isSuccessful) {
                    successMessage.postValue("Succursale ajoutée avec succès")
                    fetchSuccursales(aut)
                } else {
                    errorMessage.postValue("Erreur lors de l'ajout de la succursale")
                }
            } catch (e: Exception) {
                errorMessage.postValue(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun modifierSuccursale(aut: String, ancienneVille: String, nouvelleVille: String, nouveauBudget: String) {
        viewModelScope.launch {
            try {
                // Supprimez la succursale avec l'ancien nom
                val deleteResponse = repository.deleteSuccursale(aut, ancienneVille)
                if (deleteResponse.isSuccessful) {
                    // Ajoutez une nouvelle succursale avec le nouveau nom
                    val addResponse = repository.addSuccursale(aut, nouvelleVille, nouveauBudget)
                    if (addResponse.isSuccessful) {
                        successMessage.postValue("Succursale mise à jour avec succès")
                    } else {
                        errorMessage.postValue("Erreur lors de l'ajout de la nouvelle succursale")
                    }
                } else {
                    errorMessage.postValue("Erreur lors de la suppression de l'ancienne succursale")
                }
            } catch (e: Exception) {
                errorMessage.postValue(e.message ?: "Erreur inconnue lors de la mise à jour")
            }
        }
    }

    fun getSuccursaleBudget(aut: String, ville: String) {
        viewModelScope.launch {
            try {
                val response = repository.getSuccursaleBudget(aut, ville)
                if (response.isSuccessful && response.body() != null) {
                    succursaleBudgetResponse.postValue(response.body())
                } else {
                    errorMessage.postValue("Erreur lors de la recherche du budget de la succursale")
                }
            } catch (e: Exception) {
                errorMessage.postValue(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun supprimerSuccursale(aut: String, ville: String) {
        viewModelScope.launch {
            try {
                val response = repository.deleteSuccursale(aut, ville)
                if (response.isSuccessful) {
                    fetchSuccursales(aut)
                } else {
                    errorMessage.postValue("Erreur lors de la suppression de la succursale")
                }
            } catch (e: Exception) {
                errorMessage.postValue(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun supprimerToutesLesSuccursales(aut: String) {
        viewModelScope.launch {
            try {
                val response = repository.deleteAllSuccursales(aut)
                if (response.isSuccessful) {
                    allSuccursalesDeleted.postValue(true)
                    refreshData(aut)
                } else {
                    errorMessage.postValue("Erreur lors de la suppression des succursales")
                }
            } catch (e: Exception) {
                errorMessage.postValue(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun refreshData(aut: String) {
        fetchSuccursales(aut)
    }
}
