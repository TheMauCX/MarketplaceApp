package com.marketplace.app.presentation.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.marketplace.app.databinding.FragmentProductoListBinding
import com.marketplace.app.presentation.viewmodel.ProductoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductoListFragment : Fragment() {

    private var _binding: FragmentProductoListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductoViewModel by viewModels()
    private lateinit var adapter: ProductoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = ProductoAdapter(
            onItemClick = { producto ->
                showMessage("Click en: ${producto.nombre}")
            },
            onEditClick = { producto ->
                showMessage("Editar: ${producto.nombre}")
            },
            onDeleteClick = { producto ->
                showDeleteConfirmation(producto)
            }
        )

        binding.recyclerViewProductos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ProductoListFragment.adapter
        }
    }

    private fun setupObservers() {
        // Observar estado de productos
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.productosState.collect { resource ->
                when (resource) {
                    is com.marketplace.app.domain.model.Resource.Loading -> {
                        showLoading(true)
                        binding.emptyView.gone()
                    }
                    is com.marketplace.app.domain.model.Resource.Success -> {
                        showLoading(false)
                        val productos = resource.data
                        adapter.submitList(productos)

                        if (productos.isEmpty()) {
                            binding.emptyView.visible()
                            binding.recyclerViewProductos.gone()
                        } else {
                            binding.emptyView.gone()
                            binding.recyclerViewProductos.visible()
                        }
                    }
                    is com.marketplace.app.domain.model.Resource.Error -> {
                        showLoading(false)
                        showError(resource.message)
                    }
                }
            }
        }

        // Observar estado de operaciones
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.operationState.collect { resource ->
                resource?.let {
                    when (it) {
                        is com.marketplace.app.domain.model.Resource.Success -> {
                            showSuccess("Operación completada")
                            viewModel.clearOperationState()
                        }
                        is com.marketplace.app.domain.model.Resource.Error -> {
                            showError(it.message)
                            viewModel.clearOperationState()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabAddProducto.setOnClickListener {
            showMessage("Agregar nuevo producto")
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadProductos()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun showDeleteConfirmation(producto: com.marketplace.app.domain.model.Producto) {
        viewModel.deleteProducto(producto.id)
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}