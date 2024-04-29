package cgodin.qc.ca.repository

import cgodin.qc.ca.model.StudentRegistrationRequest
import cgodin.qc.ca.model.StudentRegistrationResponse
import cgodin.qc.ca.model.SuccursaleRetraitRequest
import cgodin.qc.ca.network.ApiService
import retrofit2.Response

class StudentRepository(private val apiService: ApiService) {

    suspend fun registerStudent(
        request: StudentRegistrationRequest
    ): Response<StudentRegistrationResponse> {
        return apiService.registerStudent(request)
    }
}
