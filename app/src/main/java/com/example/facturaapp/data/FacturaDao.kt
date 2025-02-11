package com.example.facturaapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FacturaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFactura(factura: FacturaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFacturas(facturas: List<FacturaEntity>) // Método para insertar múltiples facturas

    @Update
    suspend fun updateFactura(factura: FacturaEntity)

    @Delete
    suspend fun deleteFactura(factura: FacturaEntity)

    @Query("SELECT * FROM facturas ORDER BY id ASC")
    fun getAllFacturas(): kotlinx.coroutines.flow.Flow<List<FacturaEntity>>

    @Query("SELECT * FROM facturas WHERE id = :id LIMIT 1")
    suspend fun getFacturaById(id: Int): FacturaEntity?
}
