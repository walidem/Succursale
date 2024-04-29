package cgodin.qc.ca.network

import cgodin.qc.ca.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/students/Connexion")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/students/Enregistrement")
    suspend fun registerStudent(@Body request: StudentRegistrationRequest): Response<StudentRegistrationResponse>

    @POST("/succursales/Succursale-Liste")
    suspend fun getSuccursaleList(@Body request: SuccursaleListRequest): Response<SuccursaleListResponse>

    @POST("/succursales/Succursale-Compte")
    suspend fun getSuccursaleAccount(@Body request: SuccursaleAccountRequest): Response<SuccursaleAccountResponse>

    @POST("/succursales/Succursale-Budget")
    suspend fun getSuccursaleBudget(@Body request: SuccursaleBudgetRequest): Response<SuccursaleBudgetResponse>

    @POST("/succursales/Succursale-Ajout")
    suspend fun addSuccursale(@Body request: SuccursaleAddRequest): Response<SuccursaleAddResponse>

    @POST("/succursales/Succursale-Retrait")
    @Headers("X-HTTP-Method-Override: DELETE")
    suspend fun deleteSuccursale(@Body request: SuccursaleRetraitRequest): Response<Unit>

    @POST("/succursales/Succursale-Suppression")
    @Headers("X-HTTP-Method-Override: DELETE")
    suspend fun deleteAllSuccursales(@Body request: SuccursaleSuppressionRequest): Response<Unit>
}
