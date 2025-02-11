package com.example.facturaapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FacturaEntity::class], version = 3, exportSchema = false)
abstract class FacturaDatabase : RoomDatabase() {
    abstract fun facturaDao(): FacturaDao
}
