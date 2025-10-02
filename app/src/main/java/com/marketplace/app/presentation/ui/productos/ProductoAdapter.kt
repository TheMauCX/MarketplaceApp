package com.marketplace.app.presentation.ui.productos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.marketplace.app.R
import com.marketplace.app.domain.model.Producto
import java.text.NumberFormat
import java.util.Locale

class ProductoAdapter(
    private val onItemClick: (Producto) -> Unit,
    private val onEditClick: (Producto) -> Unit,
    private val onDeleteClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    private var productos: List<Producto> = emptyList()
    private val numberFormat = NumberFormat.getCurrencyInstance(Locale("es", "PE"))

    fun submitList(newProductos: List<Producto>) {
        productos = newProductos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(productos[position])
    }

    override fun getItemCount(): Int = productos.size

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tv_descripcion)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tv_precio)
        private val tvStock: TextView = itemView.findViewById(R.id.tv_stock)
        private val chipCategoria: Chip = itemView.findViewById(R.id.chip_categoria)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)

        fun bind(producto: Producto) {
            tvNombre.text = producto.nombre
            tvDescripcion.text = producto.descripcion ?: "Sin descripción"
            tvPrecio.text = numberFormat.format(producto.precio)
            tvStock.text = "Stock: ${producto.stock}"

            // Categoría
            chipCategoria.text = producto.categoria ?: "Sin categoría"

            // Color del stock
            if (producto.stock > 10) {
                tvStock.setTextColor(itemView.context.getColor(R.color.green_500))
            } else if (producto.stock > 0) {
                tvStock.setTextColor(itemView.context.getColor(R.color.orange_500))
            } else {
                tvStock.setTextColor(itemView.context.getColor(R.color.red_500))
            }

            // Click en el item
            itemView.setOnClickListener {
                onItemClick(producto)
            }

            // Click en editar
            btnEdit.setOnClickListener {
                onEditClick(producto)
            }

            // Click en eliminar
            btnDelete.setOnClickListener {
                onDeleteClick(producto)
            }
        }
    }
}