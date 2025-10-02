package com.marketplace.app.domain.repository

import com.marketplace.app.domain.model.Resource
import com.marketplace.app.domain.model.Producto
import kotlinx.coroutines.flow.Flow

interface ProductoRepository {

    fun getProductos(): Flow<Resource<List<Producto>>>
    fun getProductosByVendedor(vendedorId: Long): Flow<Resource<List<Producto>>>
    suspend fun getProductoById(id: Long): Resource<Producto>
    suspend fun createProducto(producto: Producto): Resource<Producto>
    suspend fun updateProducto(producto: Producto): Resource<Producto>
    suspend fun deleteProducto(id: Long): Resource<Unit>
}