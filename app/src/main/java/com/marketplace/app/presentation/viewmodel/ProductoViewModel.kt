package com.marketplace.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.app.domain.model.Resource
import com.marketplace.app.domain.model.Producto
import com.marketplace.app.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductoViewModel @Inject constructor(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _productosState = MutableStateFlow<Resource<List<Producto>>>(Resource.Loading)
    val productosState: StateFlow<Resource<List<Producto>>> = _productosState.asStateFlow()

    private val _productosByVendedorState = MutableStateFlow<Resource<List<Producto>>>(Resource.Loading)
    val productosByVendedorState: StateFlow<Resource<List<Producto>>> = _productosByVendedorState.asStateFlow()

    private val _operationState = MutableStateFlow<Resource<Unit>?>(null)
    val operationState: StateFlow<Resource<Unit>?> = _operationState.asStateFlow()

    private val _selectedProducto = MutableStateFlow<Producto?>(null)
    val selectedProducto: StateFlow<Producto?> = _selectedProducto.asStateFlow()

    init {
        loadProductos()
    }

    fun loadProductos() {
        viewModelScope.launch {
            productoRepository.getProductos().collect { resource ->
                _productosState.value = resource
            }
        }
    }

    fun loadProductosByVendedor(vendedorId: Long) {
        viewModelScope.launch {
            productoRepository.getProductosByVendedor(vendedorId).collect { resource ->
                _productosByVendedorState.value = resource
            }
        }
    }

    fun createProducto(producto: Producto) {
        viewModelScope.launch {
            _operationState.value = Resource.Loading
            val result = productoRepository.createProducto(producto)
            _operationState.value = when (result) {
                is Resource.Success -> Resource.Success(Unit)
                is Resource.Error -> Resource.Error(result.message)
                else -> null
            }

            if (result is Resource.Success) {
                loadProductos()
            }
        }
    }

    fun updateProducto(producto: Producto) {
        viewModelScope.launch {
            _operationState.value = Resource.Loading
            val result = productoRepository.updateProducto(producto)
            _operationState.value = when (result) {
                is Resource.Success -> Resource.Success(Unit)
                is Resource.Error -> Resource.Error(result.message)
                else -> null
            }

            if (result is Resource.Success) {
                loadProductos()
            }
        }
    }

    fun deleteProducto(id: Long) {
        viewModelScope.launch {
            _operationState.value = Resource.Loading
            val result = productoRepository.deleteProducto(id)
            _operationState.value = when (result) {
                is Resource.Success -> Resource.Success(Unit)
                is Resource.Error -> Resource.Error(result.message)
                else -> null
            }

            if (result is Resource.Success) {
                loadProductos()
            }
        }
    }

    fun selectProducto(producto: Producto?) {
        _selectedProducto.value = producto
    }

    fun clearOperationState() {
        _operationState.value = null
    }
}