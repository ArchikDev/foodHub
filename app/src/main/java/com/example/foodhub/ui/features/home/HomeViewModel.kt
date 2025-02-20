package com.example.foodhub.ui.features.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.data.FoodApi
import com.example.foodhub.data.models.Category
import com.example.foodhub.data.models.Restaurant
import com.example.foodhub.data.remote.ApiResponse
import com.example.foodhub.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val foodApi: FoodApi): ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeScreenNavigationEvents?>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    var categories = emptyList<Category>()
    var restaurants = emptyList<Restaurant>()

    init {
        viewModelScope.launch {
            categories = getCategories()
            restaurants = getPopularRestaurants()

            if (categories.isNotEmpty() && restaurants.isNotEmpty()) {
                _uiState.value = HomeScreenState.Success
            } else {
                _uiState.value = HomeScreenState.Empty
            }
        }
    }

    private suspend fun getCategories():  List<Category> {
        var list = emptyList<Category>()

        val response = safeApiCall {
            foodApi.getCategories()
        }

        when (response) {
            is ApiResponse.Success -> {
                list = response.data
            }

            else -> {

            }
        }

        return list
    }

    private suspend fun getPopularRestaurants(): List<Restaurant> {
        var list = emptyList<Restaurant>()
        val response = safeApiCall {
            foodApi.getRestaurants()
        }

        when (response) {
            is ApiResponse.Success -> {
                list = response.data
            }
            else -> {

            }
        }

        return list
    }

    fun onRestaurantSelected(it: Restaurant) {
        viewModelScope.launch {
            _navigationEvent.emit(
                HomeScreenNavigationEvents.NavigateToDetail(
                    it.name,
                    it.image_url,
                    it.id
                )
            )
        }
    }


    sealed class HomeScreenState {
        data object Loading: HomeScreenState()
        data object Empty: HomeScreenState()
        data object Success: HomeScreenState()
    }

    sealed class HomeScreenNavigationEvents {
        data class NavigateToDetail(
            val name: String,
            val imageUrl: String,
            val id: String
        ): HomeScreenNavigationEvents()
    }

}