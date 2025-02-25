package com.example.foodhub.ui.features.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.data.FoodApi
import com.example.foodhub.data.models.CartItem
import com.example.foodhub.data.models.CartResponse
import com.example.foodhub.data.remote.ApiResponse
import com.example.foodhub.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(val foodApi: FoodApi): ViewModel() {

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CartEvent>()
    val event = _event.asSharedFlow()

    init {
        getCart()
    }

    fun getCart() {
        viewModelScope.launch {
            _uiState.value = CartUiState.Loading

            val res = safeApiCall {
                foodApi.getCart()
            }

            when(res) {
                is ApiResponse.Success -> {
                    _uiState.value = CartUiState.Success(res.data)
                }
                is ApiResponse.Error -> {
                    _uiState.value = CartUiState.Error(res.message)
                }

                else -> {
                    _uiState.value = CartUiState.Error("An error occurred")
                }
            }
        }
    }

    fun incrementQuantity(cartItem: CartItem, quantity: Int) {

    }

    fun decrementQuantity(cartItem: CartItem, quantity: Int) {

    }

    fun removeItem(cartItem: CartItem) {

    }

    fun checkout() {

    }


    sealed class CartUiState {
        data object Nothing: CartUiState()
        data object Loading: CartUiState()
        data class Success(val data: CartResponse): CartUiState()
        data class Error(val message: String) : CartUiState()
    }

    sealed class CartEvent {
        data object showErrorDialog: CartEvent()
        data object onCheckout: CartEvent()
    }
}