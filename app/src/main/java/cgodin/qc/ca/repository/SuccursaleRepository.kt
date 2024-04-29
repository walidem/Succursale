package cgodin.qc.ca.repository

import androidx.lifecycle.LiveData
import cgodin.qc.ca.model.*
import cgodin.qc.ca.network.ApiService
import retrofit2.Response

class SuccursaleRepository( val succursaleDao: SuccursaleDao, private val apiService: ApiService) {

    suspend fun updateFavoris(succursale: Succursale) {
        succursaleDao.updateFavoris(succursale.Ville, succursale.isFavoris)
    }

    fun getFavoris(): LiveData<List<Succursale>> = succursaleDao.getFavoris()


    // Remote API operations
    suspend fun getSuccursaleList(aut: String): Response<SuccursaleListResponse> {
        return apiService.getSuccursaleList(SuccursaleListRequest(aut))
    }

    suspend fun getSuccursaleBudget(
        aut: String,
        ville: String
    ): Response<SuccursaleBudgetResponse> {
        val request = SuccursaleBudgetRequest(aut, ville)
        return apiService.getSuccursaleBudget(request)
    }

    suspend fun addSuccursale(
        aut: String,
        ville: String,
        budget: String
    ): Response<SuccursaleAddResponse> {
        val request = SuccursaleAddRequest(aut, ville, budget)
        return apiService.addSuccursale(request)
    }

    suspend fun deleteSuccursale(aut: String, ville: String): Response<Unit> {
        val request = SuccursaleRetraitRequest(aut, ville)
        return apiService.deleteSuccursale(request)
    }

    suspend fun deleteAllSuccursales(aut: String): Response<Unit> {
        val request = SuccursaleSuppressionRequest(aut)
        return apiService.deleteAllSuccursales(request)
    }
}
