package cgodin.qc.ca.repository

import cgodin.qc.ca.model.LoginRequest
import cgodin.qc.ca.model.LoginResponse
import cgodin.qc.ca.network.ApiService
import retrofit2.Response


class AuthRepository(private val apiService: ApiService) {
    suspend fun loginUser(username: String, password: String): Response<LoginResponse> {
        return apiService.loginUser(LoginRequest(username, password))
    }
}
