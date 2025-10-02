package com.marketplace.app.data.local.dao

import androidx.room.*
import com.marketplace.app.data.local.entities.VendedorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VendedorDao {

    @Query("SELECT * FROM vendedores")
    fun getAllVendedores(): Flow<List<VendedorEntity>>

    @Query("SELECT * FROM vendedores WHERE id = :id")
    suspend fun getVendedorById(id: Int): VendedorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendedor(vendedor: VendedorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vendedores: List<VendedorEntity>)

    @Update
    suspend fun updateVendedor(vendedor: VendedorEntity)

    @Delete
    suspend fun deleteVendedor(vendedor: VendedorEntity)

    @Query("DELETE FROM vendedores WHERE id = :id")
    suspend fun deleteVendedorById(id: Int)

    @Query("SELECT * FROM vendedores WHERE sincronizado = 0")
    suspend fun getUnsyncedVendedores(): List<VendedorEntity>

    @Query("DELETE FROM vendedores")
    suspend fun deleteAll()
}