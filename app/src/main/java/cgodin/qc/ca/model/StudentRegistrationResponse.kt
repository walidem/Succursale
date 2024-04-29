package cgodin.qc.ca.model

import com.google.gson.annotations.SerializedName

data class StudentRegistrationResponse(
    @SerializedName("statut") val status: String,
    @SerializedName("error") val error: ErrorResponse?
)

data class ErrorResponse(
    @SerializedName("message") val message: String
)

