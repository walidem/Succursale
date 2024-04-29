package cgodin.qc.ca.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Succursale::class], version = 2)
abstract class SuccursaleDatabase : RoomDatabase() {
    abstract fun succursaleDao(): SuccursaleDao

    companion object {
        @Volatile
        private var INSTANCE: SuccursaleDatabase? = null

        fun getDatabase(context: Context): SuccursaleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SuccursaleDatabase::class.java,
                    "succursaledatabase"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

