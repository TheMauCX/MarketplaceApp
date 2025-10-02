package com.marketplace.app.domain.repository

import com.marketplace.app.data.local.dao.ProductoDao
import com.marketplace.app.data.remote.api.ProductoApiService
import com.marketplace.app.domain.model.Resource
import com.marketplace.app.domain.model.Producto
import com.marketplace.app.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductoRepositoryImpl @Inject constructor(
    private val productoDao: ProductoDao,
    private val productoApiService: ProductoApiService
) : ProductoRepository {

    override fun getProductos(): Flow<Resource<List<Producto>>> = flow {
        emit(Resource.Loading)

        try {
            // Primero obtener de la base de datos local
            productoDao.getAllProductos().collect { entities ->
                val productos = entities.map { it.toDomain() }
                emit(Resource.Success(productos))
            }

            // Luego intentar sincronizar con el servidor
            try {
                val response = productoApiService.getAllProductos()
                if (response.isSuccessful && response.body()?.success == true) {
                    val productosDTO = response.body()!!.data!!
                    val entities = productosDTO.map {
                        com.marketplace.app.data.local.entities.ProductoEntity.fromDomain(it.toDomain())
                    }
                    productoDao.deleteAll()
                    productoDao.insertAll(entities)
                }
            } catch (e: Exception) {
                // Ignorar errores de sincronización, usar datos locales
            }

        } catch (e: Exception) {
            emit(Resource.Error("Error cargando productos: ${e.message}"))
        }
    }

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
                val entity = com.marketplace.app.data.local.entities.ProductoEntity.fromDomain(createdProducto)
                productoDao.insertProducto(entity)
                Resource.Success(createdProducto)
            } else {
                Resource.Error("Error creando producto en el servidor")
            }
        } catch (e: Exception) {
            // Fallback: guardar localmente
            try {
                val entity = com.marketplace.app.data.local.entities.ProductoEntity.fromDomain(producto)
                val id = productoDao.insertProducto(entity)
                val localProducto = producto.copy(id = id.toLong())
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
                val entity = com.marketplace.app.data.local.entities.ProductoEntity.fromDomain(updatedProducto)
                productoDao.updateProducto(entity)
                Resource.Success(updatedProducto)
            } else {
                Resource.Error("Error actualizando producto en el servidor")
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
                Resource.Error("Error eliminando producto en el servidor")
            }
        } catch (e: Exception) {
            Resource.Error("Error eliminando producto: ${e.message}")
        }
    }
}