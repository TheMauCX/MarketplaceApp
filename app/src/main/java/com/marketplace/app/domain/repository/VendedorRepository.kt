package com.marketplace.app.domain.repository

import com.marketplace.app.domain.model.Resource
import com.marketplace.app.domain.model.Vendedor
import kotlinx.coroutines.flow.Flow

interface VendedorRepository {

    fun getVendedores(): Flow<Resource<List<Vendedor>>>
    suspend fun getVendedorById(id: Long): Resource<Vendedor>
    suspend fun createVendedor(vendedor: Vendedor): Resource<Vendedor>
    suspend fun updateVendedor(vendedor: Vendedor): Resource<Vendedor>
    suspend fun deleteVendedor(id: Long): Resource<Unit>
}