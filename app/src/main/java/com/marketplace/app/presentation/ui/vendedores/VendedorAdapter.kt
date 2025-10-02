package com.marketplace.app.presentation.ui.vendedores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.marketplace.app.R
import com.marketplace.app.domain.model.Vendedor

class VendedorAdapter(
    private val onItemClick: (Vendedor) -> Unit,
    private val onEditClick: (Vendedor) -> Unit,
    private val onDeleteClick: (Vendedor) -> Unit
) : RecyclerView.Adapter<VendedorAdapter.VendedorViewHolder>() {

    private var vendedores: List<Vendedor> = emptyList()

    // CORRECCIÓN: Método para actualizar la lista
    fun submitList(newVendedores: List<Vendedor>) {
        vendedores = newVendedores
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendedorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vendedor, parent, false)
        return VendedorViewHolder(view)
    }

    override fun onBindViewHolder(holder: VendedorViewHolder, position: Int) {
        holder.bind(vendedores[position])
    }

    override fun getItemCount(): Int = vendedores.size

    inner class VendedorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre)
        private val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        private val tvTelefono: TextView = itemView.findViewById(R.id.tv_telefono)
        private val chipEstado: Chip = itemView.findViewById(R.id.chip_estado)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)

        fun bind(vendedor: Vendedor) {
            tvNombre.text = vendedor.nombre
            tvEmail.text = vendedor.email
            tvTelefono.text = vendedor.telefono ?: "Sin teléfono"

            // Estado
            if (vendedor.estado) {
                chipEstado.text = "Activo"
                chipEstado.setChipBackgroundColorResource(R.color.green_500)
            } else {
                chipEstado.text = "Inactivo"
                chipEstado.setChipBackgroundColorResource(R.color.grey_500)
            }

            // Click en el item
            itemView.setOnClickListener {
                onItemClick(vendedor)
            }

            // Click en editar
            btnEdit.setOnClickListener {
                onEditClick(vendedor)
            }

            // Click en eliminar
            btnDelete.setOnClickListener {
                onDeleteClick(vendedor)
            }
        }
    }
}