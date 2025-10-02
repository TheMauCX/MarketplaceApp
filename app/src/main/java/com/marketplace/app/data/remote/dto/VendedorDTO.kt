package com.marketplace.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.marketplace.app.domain.model.Vendedor

data class VendedorDTO(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("telefono")
    val telefono: String? = null,

    @SerializedName("direccion")
    val direccion: String? = null,

    @SerializedName("rucDni")
    val rucDni: String? = null,

    @SerializedName("estado")
    val estado: Boolean? = true,

    @SerializedName("fechaCreacion")
    val fechaCreacion: String? = null
) {
    fun toDomain(): Vendedor {
        return Vendedor(
            id = id ?: 0,
            nombre = nombre,
            email = email,
            telefono = telefono,
            direccion = direccion,
            rucDni = rucDni,
            estado = estado ?: true,
            fechaCreacion = fechaCreacion
        )
    }

    companion object {
        fun fromDomain(vendedor: Vendedor): VendedorDTO {
            return VendedorDTO(
                id = if (vendedor.id > 0) vendedor.id else null,
                nombre = vendedor.nombre,
                email = vendedor.email,
                telefono = vendedor.telefono,
                direccion = vendedor.direccion,
                rucDni = vendedor.rucDni,
                estado = vendedor.estado
            )
        }
    }
}