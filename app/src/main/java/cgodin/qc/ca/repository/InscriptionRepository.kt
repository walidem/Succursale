package cgodin.qc.ca.repository

import cgodin.qc.ca.model.StudentRegistrationRequest
import cgodin.qc.ca.model.StudentRegistrationResponse
import cgodin.qc.ca.network.ApiService
import retrofit2.Response

class InscriptionRepository(private val apiService: ApiService) {
    suspend fun inscrire(matricule: String, motDePasse: String, nom: String, prenom: String): Response<StudentRegistrationResponse> {
        val request = StudentRegistrationRequest(matricule, motDePasse, nom, prenom)
        return apiService.registerStudent(request)
    }
}


