package com.marketplace.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import com.marketplace.app.domain.model.Producto
import java.math.BigDecimal

@Entity(
    tableName = "productos",
    foreignKeys = [
        ForeignKey(
            entity = VendedorEntity::class,
            parentColumns = ["id"],
            childColumns = ["vendedor_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "descripcion")
    val descripcion: String?,

    @ColumnInfo(name = "precio")
    val precio: BigDecimal,

    @ColumnInfo(name = "stock")
    val stock: Int = 0,

    @ColumnInfo(name = "vendedor_id")
    val vendedorId: Int,

    @ColumnInfo(name = "categoria")
    val categoria: String?,

    @ColumnInfo(name = "imagen_url")
    val imagenUrl: String?,

    @ColumnInfo(name = "sincronizado")
    val sincronizado: Boolean = false,

    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: String?
) {
    fun toDomain(): Producto {
        return Producto(
            id = id.toLong(),
            nombre = nombre,
            descripcion = descripcion,
            precio = precio,
            stock = stock,
            vendedorId = vendedorId.toLong(),
            categoria = categoria,
            imagenUrl = imagenUrl,
            fechaCreacion = fechaCreacion
        )
    }

    companion object {
        fun fromDomain(producto: Producto): ProductoEntity {
            return ProductoEntity(
                id = if (producto.id > 0) producto.id.toInt() else 0,
                nombre = producto.nombre,
                descripcion = producto.descripcion,
                precio = producto.precio,
                stock = producto.stock,
                vendedorId = producto.vendedorId.toInt(),
                categoria = producto.categoria,
                imagenUrl = producto.imagenUrl,
                fechaCreacion = producto.fechaCreacion
            )
        }
    }
}