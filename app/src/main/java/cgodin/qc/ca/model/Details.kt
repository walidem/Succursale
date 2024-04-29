package cgodin.qc.ca.model

import com.google.gson.annotations.SerializedName

data class Details(
    @SerializedName("Mat") val Mat: String,
    @SerializedName("Nom") val Nom: String,
    @SerializedName("Prenom") val Prenom: String,
    @SerializedName("token") val token: String
)
