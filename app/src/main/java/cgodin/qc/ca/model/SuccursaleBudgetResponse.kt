package cgodin.qc.ca.model

data class SuccursaleBudgetResponse(
    val statut: String,
    val succursale: SuccursaleDetails
)

data class SuccursaleDetails(
    val _id: String,
    val AccessMDP: Long,
    val Ville: String,
    val Budget: Int,
    val __v: Int
)
