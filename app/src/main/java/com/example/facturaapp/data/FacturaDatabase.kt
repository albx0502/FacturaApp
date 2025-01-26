package com.example.facturaapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FacturaEntity::class], version = 1, exportSchema = false)
abstract class FacturaDatabase : RoomDatabase() {

    abstract fun facturaDao(): FacturaDao

    companion object {
        @Volatile
        private var INSTANCE: FacturaDatabase? = null

        fun getDatabase(context: Context): FacturaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FacturaDatabase::class.java,
                    "factura_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
