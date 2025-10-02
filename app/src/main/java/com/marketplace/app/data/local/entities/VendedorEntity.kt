package com.marketplace.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.marketplace.app.domain.model.Vendedor

@Entity(tableName = "vendedores")
data class VendedorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "telefono")
    val telefono: String?,

    @ColumnInfo(name = "direccion")
    val direccion: String?,

    @ColumnInfo(name = "ruc_dni")
    val rucDni: String?,

    @ColumnInfo(name = "estado")
    val estado: Boolean = true,

    @ColumnInfo(name = "sincronizado")
    val sincronizado: Boolean = false,

    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: String?
) {
    fun toDomain(): Vendedor {
        return Vendedor(
            id = id.toLong(),
            nombre = nombre,
            email = email,
            telefono = telefono,
            direccion = direccion,
            rucDni = rucDni,
            estado = estado,
            fechaCreacion = fechaCreacion
        )
    }

    companion object {
        fun fromDomain(vendedor: Vendedor): VendedorEntity {
            return VendedorEntity(
                id = if (vendedor.id > 0) vendedor.id.toInt() else 0,
                nombre = vendedor.nombre,
                email = vendedor.email,
                telefono = vendedor.telefono,
                direccion = vendedor.direccion,
                rucDni = vendedor.rucDni,
                estado = vendedor.estado,
                fechaCreacion = vendedor.fechaCreacion
            )
        }
    }
}