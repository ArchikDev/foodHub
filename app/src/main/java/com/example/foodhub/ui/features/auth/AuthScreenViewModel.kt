package com.example.foodhub.ui.features.auth

import androidx.lifecycle.viewModelScope
import com.example.foodhub.FoodHubSession
import com.example.foodhub.data.FoodApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    override val foodApi: FoodApi,
    val session: FoodHubSession
): BaseAuthViewModel(foodApi) {

    private val _uiState = MutableStateFlow<AuthEvent>(AuthEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<AuthNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    sealed class AuthNavigationEvent {
        data object NavigateToSignUp: AuthNavigationEvent()
        data object NavigateToHome: AuthNavigationEvent()
        data object ShowErrorDialog: AuthNavigationEvent()
    }

    sealed class AuthEvent {
        data object Nothing: AuthEvent()
        data object Success: AuthEvent()
        data object Error: AuthEvent()
        data object Loading: AuthEvent()
    }

    override fun loading() {
        viewModelScope.launch {
            _uiState.value = AuthEvent.Loading
        }
    }

    override fun onGoogleError(msg: String) {
        viewModelScope.launch {
            error = "Google Sign In Failed"
            errorDescription = msg
            _uiState.value = AuthEvent.Error
            _navigationEvent.emit(AuthNavigationEvent.ShowErrorDialog)
        }
    }

    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            session.storeToken(token)
            _uiState.value = AuthEvent.Success
            _navigationEvent.emit(AuthNavigationEvent.NavigateToHome)
        }
    }

}