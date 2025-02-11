package com.example.facturaapp.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var INSTANCE: FacturaDatabase? = null

    fun getDatabase(context: Context): FacturaDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                FacturaDatabase::class.java,
                "factura_database"
            )
                .fallbackToDestructiveMigration() // Esto asegura que los cambios en la base de datos no rompan la app.
                .build()
            INSTANCE = instance
            instance
        }
    }
}
