package com.marketplace.app.presentation.ui.vendedores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.marketplace.app.databinding.FragmentVendedorListBinding
import com.marketplace.app.presentation.viewmodel.VendedorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VendedorListFragment : Fragment() {

    private var _binding: FragmentVendedorListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VendedorViewModel by viewModels()
    private lateinit var adapter: VendedorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVendedorListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = VendedorAdapter(
            onItemClick = { vendedor ->
                showMessage("Click en: ${vendedor.nombre}")
            },
            onEditClick = { vendedor ->
                showMessage("Editar: ${vendedor.nombre}")
            },
            onDeleteClick = { vendedor ->
                showDeleteConfirmation(vendedor)
            }
        )

        binding.recyclerViewVendedores.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@VendedorListFragment.adapter
        }
    }

    private fun setupObservers() {
        // Observar estado de vendedores
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.vendedoresState.collect { resource ->
                when (resource) {
                    is com.marketplace.app.domain.model.Resource.Loading -> {
                        showLoading(true)
                        binding.emptyView.gone()
                    }
                    is com.marketplace.app.domain.model.Resource.Success -> {
                        showLoading(false)
                        val vendedores = resource.data
                        // CORRECCIÓN: Usar submitList en el adapter
                        adapter.submitList(vendedores)

                        if (vendedores.isEmpty()) {
                            binding.emptyView.visible()
                            binding.recyclerViewVendedores.gone()
                        } else {
                            binding.emptyView.gone()
                            binding.recyclerViewVendedores.visible()
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
        binding.fabAddVendedor.setOnClickListener {
            showMessage("Agregar nuevo vendedor")
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadVendedores()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun showDeleteConfirmation(vendedor: com.marketplace.app.domain.model.Vendedor) {
        // Implementar diálogo de confirmación más adelante
        viewModel.deleteVendedor(vendedor.id)
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

// Extension functions para visibility
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}