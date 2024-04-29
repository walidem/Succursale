package cgodin.qc.ca.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SuccursaleDao {
    @Query("SELECT * FROM succursale_table WHERE ville = :ville")
    suspend fun getSuccursaleByVille(ville: String): Succursale?

    @Update
    suspend fun updateSuccursale(succursale: Succursale)

    @Query("DELETE FROM succursale_table")
    suspend fun clearAll()

    @Query("UPDATE succursale_table SET isFavoris = :isFavoris WHERE Ville = :ville")
    suspend fun updateFavoris(ville: String, isFavoris: Boolean)

    @Query("SELECT * FROM succursale_table WHERE isFavoris = 1")
    fun getFavoris(): LiveData<List<Succursale>>

    @Query("DELETE FROM succursale_table WHERE ville = :ville")
    suspend fun deleteFavorite(ville: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuccursale(succursale: Succursale)

    @Transaction
    suspend fun refreshSuccursales(succursales: List<Succursale>) {
        clearAll()
        succursales.forEach {
            Log.d("DAO", "Inserting Succursale: $it")
            insertSuccursale(it)
        }
    }
}
