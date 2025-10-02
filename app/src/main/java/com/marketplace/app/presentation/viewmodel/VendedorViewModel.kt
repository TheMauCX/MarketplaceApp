package com.marketplace.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.app.domain.model.Resource
import com.marketplace.app.domain.model.Vendedor
import com.marketplace.app.domain.repository.VendedorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VendedorViewModel @Inject constructor(
    private val vendedorRepository: VendedorRepository
) : ViewModel() {

    private val _vendedoresState = MutableStateFlow<Resource<List<Vendedor>>>(Resource.Loading)
    val vendedoresState: StateFlow<Resource<List<Vendedor>>> = _vendedoresState.asStateFlow()

    private val _operationState = MutableStateFlow<Resource<Unit>?>(null)
    val operationState: StateFlow<Resource<Unit>?> = _operationState.asStateFlow()

    private val _selectedVendedor = MutableStateFlow<Vendedor?>(null)
    val selectedVendedor: StateFlow<Vendedor?> = _selectedVendedor.asStateFlow()

    init {
        loadVendedores()
    }

    fun loadVendedores() {
        viewModelScope.launch {
            vendedorRepository.getVendedores().collect { resource ->
                _vendedoresState.value = resource
            }
        }
    }

    fun createVendedor(vendedor: Vendedor) {
        viewModelScope.launch {
            _operationState.value = Resource.Loading
            val result = vendedorRepository.createVendedor(vendedor)
            _operationState.value = when (result) {
                is Resource.Success -> Resource.Success(Unit)
                is Resource.Error -> Resource.Error(result.message)
                else -> null
            }

            if (result is Resource.Success) {
                loadVendedores()
            }
        }
    }

    fun updateVendedor(vendedor: Vendedor) {
        viewModelScope.launch {
            _operationState.value = Resource.Loading
            val result = vendedorRepository.updateVendedor(vendedor)
            _operationState.value = when (result) {
                is Resource.Success -> Resource.Success(Unit)
                is Resource.Error -> Resource.Error(result.message)
                else -> null
            }

            if (result is Resource.Success) {
                loadVendedores()
            }
        }
    }

    fun deleteVendedor(id: Long) {
        viewModelScope.launch {
            _operationState.value = Resource.Loading
            val result = vendedorRepository.deleteVendedor(id)
            _operationState.value = when (result) {
                is Resource.Success -> Resource.Success(Unit)
                is Resource.Error -> Resource.Error(result.message)
                else -> null
            }

            if (result is Resource.Success) {
                loadVendedores()
            }
        }
    }

    fun selectVendedor(vendedor: Vendedor?) {
        _selectedVendedor.value = vendedor
    }

    fun clearOperationState() {
        _operationState.value = null
    }
}