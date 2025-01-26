package com.example.facturaapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FacturaDao {
    // Insertar una nueva factura
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFactura(factura: FacturaEntity)

    // Actualizar una factura existente
    @Update
    suspend fun updateFactura(factura: FacturaEntity)

    // Borrar una factura
    @Delete
    suspend fun deleteFactura(factura: FacturaEntity)

    // Obtener todas las facturas
    @Query("SELECT * FROM facturas ORDER BY id ASC")
    fun getAllFacturas(): Flow<List<FacturaEntity>>

    // Obtener una factura por ID
    @Query("SELECT * FROM facturas WHERE id = :id LIMIT 1")
    suspend fun getFacturaById(id: Int): FacturaEntity?
}
