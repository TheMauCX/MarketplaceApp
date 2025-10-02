package com.marketplace.app.data.local.dao

import androidx.room.*
import com.marketplace.app.data.local.entities.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos")
    fun getAllProductos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: Int): ProductoEntity?

    @Query("SELECT * FROM productos WHERE vendedor_id = :vendedorId")
    fun getProductosByVendedor(vendedorId: Int): Flow<List<ProductoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productos: List<ProductoEntity>)

    @Update
    suspend fun updateProducto(producto: ProductoEntity)

    @Delete
    suspend fun deleteProducto(producto: ProductoEntity)

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun deleteProductoById(id: Int)

    @Query("SELECT * FROM productos WHERE sincronizado = 0")
    suspend fun getUnsyncedProductos(): List<ProductoEntity>

    @Query("DELETE FROM productos")
    suspend fun deleteAll()
}