package com.example.facturaapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FacturaEntity::class], version = 1)
abstract class FacturaDatabase : RoomDatabase() {
    abstract fun facturaDao(): FacturaDao
}