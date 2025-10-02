package com.marketplace.app.data.remote.api

import com.marketplace.app.data.remote.dto.ApiResponse
import com.marketplace.app.data.remote.dto.ProductoDTO
import retrofit2.Response
import retrofit2.http.*

interface ProductoApiService {

    @GET("productos")
    suspend fun getAllProductos(): Response<ApiResponse<List<ProductoDTO>>>

    @GET("productos/{id}")
    suspend fun getProductoById(@Path("id") id: Long): Response<ApiResponse<ProductoDTO>>

    @GET("productos/vendedor/{vendedorId}")
    suspend fun getProductosByVendedor(@Path("vendedorId") vendedorId: Long): Response<ApiResponse<List<ProductoDTO>>>

    @POST("productos")
    suspend fun createProducto(@Body producto: ProductoDTO): Response<ApiResponse<ProductoDTO>>

    @PUT("productos/{id}")
    suspend fun updateProducto(
        @Path("id") id: Long,
        @Body producto: ProductoDTO
    ): Response<ApiResponse<ProductoDTO>>

    @DELETE("productos/{id}")
    suspend fun deleteProducto(@Path("id") id: Long): Response<ApiResponse<Void>>
}