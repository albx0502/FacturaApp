package com.example.facturaapp.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var instance: FacturaDatabase? = null

    fun getDatabase(context: Context): FacturaDatabase {
        return instance ?: synchronized(this){
            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                FacturaDatabase::class.java,
                "factura_db"
            ).build()
            instance = newInstance
            newInstance
        }
    }
}