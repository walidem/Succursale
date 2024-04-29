package cgodin.qc.ca.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("statut") val status: String,
    @SerializedName("student") val student: Details
)