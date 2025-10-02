package com.marketplace.app.domain.repository

import com.marketplace.app.data.local.dao.VendedorDao
import com.marketplace.app.data.local.entities.VendedorEntity
import com.marketplace.app.data.remote.api.VendedorApiService
import com.marketplace.app.domain.model.Resource
import com.marketplace.app.domain.model.Vendedor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VendedorRepositoryImpl @Inject constructor(
    private val vendedorDao: VendedorDao,
    private val vendedorApiService: VendedorApiService
) : VendedorRepository {

    override fun getVendedores(): Flow<Resource<List<Vendedor>>> = flow {
        emit(Resource.Loading)

        try {
            // Intentar obtener del servidor primero
            try {
                val response = vendedorApiService.getAllVendedores()

                if (response.isSuccessful && response.body()?.success == true) {
                    val vendedoresDTO = response.body()!!.data ?: emptyList()

                    // Guardar en base de datos local
                    val entities = vendedoresDTO.map { dto ->
                        VendedorEntity.fromDomain(dto.toDomain())
                    }
                    vendedorDao.deleteAll()
                    vendedorDao.insertAll(entities)

                    // Emitir los datos del servidor
                    val vendedores = vendedoresDTO.map { it.toDomain() }
                    emit(Resource.Success(vendedores))
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

    fun loadFromLocal(): Flow<Resource<List<Vendedor>>> = flow {
        try {
            vendedorDao.getAllVendedores().collect { entities ->
                val vendedores = entities.map { it.toDomain() }
                if (vendedores.isNotEmpty()) {
                    emit(Resource.Success(vendedores)) // ✅ ahora sí puedes usar emit
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error cargando datos locales: ${e.message}"))
        }
    }

    override suspend fun getVendedorById(id: Long): Resource<Vendedor> {
        return try {
            val entity = vendedorDao.getVendedorById(id.toInt())
            if (entity != null) {
                Resource.Success(entity.toDomain())
            } else {
                Resource.Error("Vendedor no encontrado")
            }
        } catch (e: Exception) {
            Resource.Error("Error obteniendo vendedor: ${e.message}")
        }
    }

    override suspend fun createVendedor(vendedor: Vendedor): Resource<Vendedor> {
        return try {
            // Intentar crear en API primero
            val response = vendedorApiService.createVendedor(
                com.marketplace.app.data.remote.dto.VendedorDTO.fromDomain(vendedor)
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val createdVendedor = response.body()!!.data!!.toDomain()
                // Guardar en base de datos local
                val entity = VendedorEntity.fromDomain(createdVendedor)
                vendedorDao.insertVendedor(entity)
                Resource.Success(createdVendedor)
            } else {
                Resource.Error("Error creando vendedor en el servidor: ${response.message()}")
            }
        } catch (e: Exception) {
            // Fallback: guardar localmente
            try {
                val entity = VendedorEntity.fromDomain(vendedor)
                val id = vendedorDao.insertVendedor(entity)
                val localVendedor = vendedor.copy(id = id)
                Resource.Success(localVendedor)
            } catch (dbException: Exception) {
                Resource.Error("Error creando vendedor: ${dbException.message}")
            }
        }
    }

    override suspend fun updateVendedor(vendedor: Vendedor): Resource<Vendedor> {
        return try {
            val response = vendedorApiService.updateVendedor(
                vendedor.id,
                com.marketplace.app.data.remote.dto.VendedorDTO.fromDomain(vendedor)
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val updatedVendedor = response.body()!!.data!!.toDomain()
                val entity = VendedorEntity.fromDomain(updatedVendedor)
                vendedorDao.updateVendedor(entity)
                Resource.Success(updatedVendedor)
            } else {
                Resource.Error("Error actualizando vendedor: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error actualizando vendedor: ${e.message}")
        }
    }

    override suspend fun deleteVendedor(id: Long): Resource<Unit> {
        return try {
            val response = vendedorApiService.deleteVendedor(id)

            if (response.isSuccessful && response.body()?.success == true) {
                vendedorDao.deleteVendedorById(id.toInt())
                Resource.Success(Unit)
            } else {
                Resource.Error("Error eliminando vendedor: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error eliminando vendedor: ${e.message}")
        }
    }
}