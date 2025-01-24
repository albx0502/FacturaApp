package com.example.facturaapp.data

import androidx.room.*

@Dao
interface FacturaDao {
    @Insert
    suspend fun insertFactura(factura: FacturaEntity)

    @Query("SELECT * FROM facturas")
    suspend fun getAllFacturas(): List<FacturaEntity>

    @Update
    suspend fun updateFactura(factura: FacturaEntity)

    @Delete
    suspend fun deleteFactura(factura: FacturaEntity)
}