package com.marketplace.app.data.remote.api

import com.marketplace.app.data.remote.dto.ApiResponse
import com.marketplace.app.data.remote.dto.VendedorDTO
import retrofit2.Response
import retrofit2.http.*

interface VendedorApiService {

    @GET("vendedores")
    suspend fun getAllVendedores(): Response<ApiResponse<List<VendedorDTO>>>

    @GET("vendedores/{id}")
    suspend fun getVendedorById(@Path("id") id: Long): Response<ApiResponse<VendedorDTO>>

    @POST("vendedores")
    suspend fun createVendedor(@Body vendedor: VendedorDTO): Response<ApiResponse<VendedorDTO>>

    @PUT("vendedores/{id}")
    suspend fun updateVendedor(
        @Path("id") id: Long,
        @Body vendedor: VendedorDTO
    ): Response<ApiResponse<VendedorDTO>>

    @DELETE("vendedores/{id}")
    suspend fun deleteVendedor(@Path("id") id: Long): Response<ApiResponse<Void>>
}