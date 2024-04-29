package cgodin.qc.ca.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "succursale_table")
data class Succursale(
    val Mat: String?,
    val MDP: String?,
    val Nom: String?,
    val Prenom: String?,
    val aut: String?,
    val Budget: String?,
    @PrimaryKey val Ville: String,
    val isFavoris: Boolean = false
)