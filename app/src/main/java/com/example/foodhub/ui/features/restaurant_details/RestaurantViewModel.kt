package com.example.foodhub.ui.features.restaurant_details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.data.FoodApi
import com.example.foodhub.data.models.FoodItem
import com.example.foodhub.data.remote.ApiResponse
import com.example.foodhub.data.remote.safeApiCall
import com.example.foodhub.ui.features.auth.login.SignInViewModel.SignInEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class RestaurantViewModel @Inject constructor(val foodApi: FoodApi) : ViewModel() {
    var error = ""
    var errorDescription = ""

    private val _uiState = MutableStateFlow<RestaurantEvent>(RestaurantEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<RestaurantNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun getFoodItem(id: String) {
        viewModelScope.launch {
            _uiState.value = RestaurantEvent.Loading

            try {
                val response = safeApiCall {
                    foodApi.getFoodItemRestaurant(id)
                }

                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.value = RestaurantEvent.Success(response.data.foodItems)
                    }
                    else -> {
                        val err = (response as? ApiResponse.Error)?.code ?: 0

                        when (err) {
                            401 -> {
                                error = "Unauthorized"
                                errorDescription = "You are not authorized to view this content"
                            }
                            404 -> {
                                error = "Not Found"
                                errorDescription = "The Restaurant was not found"
                            }
                            else -> {
                                error = "Error"
                                errorDescription = "An Error occured"
                            }
                        }

                        _uiState.value = RestaurantEvent.Error
                        _navigationEvent.emit(RestaurantNavigationEvent.ShowErrorDialog)

                    }
                }
            } catch (e: Exception) {
                _uiState.value = RestaurantEvent.Error
                _navigationEvent.emit(RestaurantNavigationEvent.ShowErrorDialog)
            }
        }
    }

    sealed class RestaurantNavigationEvent {
        data object GoBack : RestaurantNavigationEvent()
        data object ShowErrorDialog : RestaurantNavigationEvent()
        data class NavigateToProduceDetails(val productID: String) : RestaurantNavigationEvent()
    }

    sealed class RestaurantEvent {
        data object Nothing : RestaurantEvent()
        data class Success(val foodItems: List<FoodItem>) : RestaurantEvent()
        data object Error : RestaurantEvent()
        data object Loading : RestaurantEvent()
    }

}