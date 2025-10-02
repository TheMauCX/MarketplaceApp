package com.marketplace.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.marketplace.app.domain.model.Producto
import java.math.BigDecimal

data class ProductoDTO(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String? = null,

    @SerializedName("precio")
    val precio: BigDecimal,

    @SerializedName("stock")
    val stock: Int = 0,

    @SerializedName("vendedorId")
    val vendedorId: Long,

    @SerializedName("categoria")
    val categoria: String? = null,

    @SerializedName("imagenUrl")
    val imagenUrl: String? = null,

    @SerializedName("fechaCreacion")
    val fechaCreacion: String? = null,

    @SerializedName("vendedorNombre")
    val vendedorNombre: String? = null,

    @SerializedName("vendedorEmail")
    val vendedorEmail: String? = null
) {
    fun toDomain(): Producto {
        return Producto(
            id = id ?: 0,
            nombre = nombre,
            descripcion = descripcion,
            precio = precio,
            stock = stock,
            vendedorId = vendedorId,
            categoria = categoria,
            imagenUrl = imagenUrl,
            fechaCreacion = fechaCreacion,
            vendedorNombre = vendedorNombre,
            vendedorEmail = vendedorEmail
        )
    }

    companion object {
        fun fromDomain(producto: Producto): ProductoDTO {
            return ProductoDTO(
                id = if (producto.id > 0) producto.id else null,
                nombre = producto.nombre,
                descripcion = producto.descripcion,
                precio = producto.precio,
                stock = producto.stock,
                vendedorId = producto.vendedorId,
                categoria = producto.categoria,
                imagenUrl = producto.imagenUrl
            )
        }
    }
}