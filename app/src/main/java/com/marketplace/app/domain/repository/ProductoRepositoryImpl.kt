package com.marketplace.app.domain.repository

import com.marketplace.app.data.local.dao.ProductoDao
import com.marketplace.app.data.local.entities.ProductoEntity
import com.marketplace.app.data.remote.api.ProductoApiService
import com.marketplace.app.domain.model.Resource
import com.marketplace.app.domain.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductoRepositoryImpl @Inject constructor(
    private val productoDao: ProductoDao,
    private val productoApiService: ProductoApiService
) : ProductoRepository {

    override fun getProductosByVendedor(vendedorId: Long): Flow<Resource<List<Producto>>> = flow {
        emit(Resource.Loading)

        try {
            productoDao.getProductosByVendedor(vendedorId.toInt()).collect { entities ->
                val productos = entities.map { it.toDomain() }
                emit(Resource.Success(productos))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error cargando productos del vendedor: ${e.message}"))
        }
    }
    override fun getProductos(): Flow<Resource<List<Producto>>> = flow {
        emit(Resource.Loading)

        try {
            // Intentar obtener del servidor primero
            try {
                val response = productoApiService.getAllProductos()

                if (response.isSuccessful && response.body()?.success == true) {
                    val productoDTO = response.body()!!.data ?: emptyList()

                    // Guardar en base de datos local
                    val entities = productoDTO.map { dto ->
                        ProductoEntity.fromDomain(dto.toDomain())
                    }
                    productoDao.deleteAll()
                    productoDao.insertAll(entities)

                    // Emitir los datos del servidor
                    val productoes = productoDTO.map { it.toDomain() }
                    emit(Resource.Success(productoes))
                } else {
                    // Si falla el servidor, intentar cargar desde local
                    emit(Resource.Error("Error del servidor: ${response.message()}"))
                    loadFromLocal()
                }
            } catch (e: Exception) {
                // Si hay error de red, cargar desde local
                emit(Resource.Error("Error de conexión: ${e.message}"))
                loadFromLocal()
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error general: ${e.message}"))
        }
    }

    fun loadFromLocal(): Flow<Resource<List<Producto>>> = flow {
        try {
            productoDao.getAllProductos().collect { entities ->
                val productoes = entities.map { it.toDomain() }
                if (productoes.isNotEmpty()) {
                    emit(Resource.Success(productoes)) // ✅ ahora sí puedes usar emit
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error cargando datos locales: ${e.message}"))
        }
    }

    override suspend fun getProductoById(id: Long): Resource<Producto> {
        return try {
            val entity = productoDao.getProductoById(id.toInt())
            if (entity != null) {
                Resource.Success(entity.toDomain())
            } else {
                Resource.Error("Producto no encontrado")
            }
        } catch (e: Exception) {
            Resource.Error("Error obteniendo producto: ${e.message}")
        }
    }

    override suspend fun createProducto(producto: Producto): Resource<Producto> {
        return try {
            // Intentar crear en API primero
            val response = productoApiService.createProducto(
                com.marketplace.app.data.remote.dto.ProductoDTO.fromDomain(producto)
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val createdProducto = response.body()!!.data!!.toDomain()
                // Guardar en base de datos local
                val entity = ProductoEntity.fromDomain(createdProducto)
                productoDao.insertProducto(entity)
                Resource.Success(createdProducto)
            } else {
                Resource.Error("Error creando producto en el servidor: ${response.message()}")
            }
        } catch (e: Exception) {
            // Fallback: guardar localmente
            try {
                val entity = ProductoEntity.fromDomain(producto)
                val id = productoDao.insertProducto(entity)
                val localProducto = producto.copy(id = id)
                Resource.Success(localProducto)
            } catch (dbException: Exception) {
                Resource.Error("Error creando producto: ${dbException.message}")
            }
        }
    }

    override suspend fun updateProducto(producto: Producto): Resource<Producto> {
        return try {
            val response = productoApiService.updateProducto(
                producto.id,
                com.marketplace.app.data.remote.dto.ProductoDTO.fromDomain(producto)
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val updatedProducto = response.body()!!.data!!.toDomain()
                val entity = ProductoEntity.fromDomain(updatedProducto)
                productoDao.updateProducto(entity)
                Resource.Success(updatedProducto)
            } else {
                Resource.Error("Error actualizando producto: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error actualizando producto: ${e.message}")
        }
    }

    override suspend fun deleteProducto(id: Long): Resource<Unit> {
        return try {
            val response = productoApiService.deleteProducto(id)

            if (response.isSuccessful && response.body()?.success == true) {
                productoDao.deleteProductoById(id.toInt())
                Resource.Success(Unit)
            } else {
                Resource.Error("Error eliminando producto: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error eliminando producto: ${e.message}")
        }
    }
}