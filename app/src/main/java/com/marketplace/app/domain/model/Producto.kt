package com.marketplace.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class Producto(
    val id: Long = 0,
    val nombre: String,
    val descripcion: String? = null,
    val precio: BigDecimal,
    val stock: Int = 0,
    val vendedorId: Long,
    val categoria: String? = null,
    val imagenUrl: String? = null,
    val fechaCreacion: String? = null,
    val vendedorNombre: String? = null,
    val vendedorEmail: String? = null
) : Parcelable