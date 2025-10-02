package com.marketplace.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Vendedor(
    val id: Long = 0,
    val nombre: String,
    val email: String,
    val telefono: String? = null,
    val direccion: String? = null,
    val rucDni: String? = null,
    val estado: Boolean = true,
    val fechaCreacion: String? = null
) : Parcelable