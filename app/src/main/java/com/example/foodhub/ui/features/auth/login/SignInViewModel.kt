package com.example.foodhub.ui.features.auth.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.foodhub.FoodHubSession
import com.example.foodhub.data.FoodApi
import com.example.foodhub.data.models.SignInRequest
import com.example.foodhub.data.remote.ApiResponse
import com.example.foodhub.data.remote.safeApiCall
import com.example.foodhub.ui.features.auth.BaseAuthViewModel
import com.example.foodhub.ui.features.auth.signup.SignUpViewModel.SignUpEvent
import com.example.foodhub.ui.features.auth.signup.SignUpViewModel.SignUpNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    override val foodApi: FoodApi,
    val session: FoodHubSession
): BaseAuthViewModel(foodApi) {

    private val _uiState = MutableStateFlow<SignInEvent>(SignInEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignInNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onSignInClick() {
        viewModelScope.launch {
            _uiState.value = SignInEvent.Loading

            val response = safeApiCall {
                foodApi.signIn(
                    SignInRequest(
                        email = email.value,
                        password = password.value
                    )
                )
            }

            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = SignInEvent.Success
                    session.storeToken(response.data.token)
                    _navigationEvent.emit(SignInNavigationEvent.NavigateToHome)
                }
                else -> {
                    val err = (response as? ApiResponse.Error)?.code ?: 0
                    error = "Sign In Failed"
                    errorDescription = "Failed to sign in"
                    _uiState.value = SignInEvent.Error

                    when (err) {
                        400 -> {
                            error = "Invalid Credintials"
                            errorDescription = "Please enter correct details"
                        }
                    }
                }
            }
        }
    }

    fun onSignUpClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SignInNavigationEvent.NavigateToSignUp)
        }
    }

    sealed class SignInNavigationEvent {
        data object NavigateToSignUp: SignInNavigationEvent()
        data object NavigateToHome: SignInNavigationEvent()
    }

    sealed class SignInEvent {
        data object Nothing: SignInEvent()
        data object Success: SignInEvent()
        data object Error: SignInEvent()
        data object Loading: SignInEvent()
    }

    override fun loading() {
        viewModelScope.launch {
            _uiState.value = SignInEvent.Loading
        }
    }

    override fun onGoogleError(msg: String) {
        viewModelScope.launch {
            error = "Google Sign In Failed"
            errorDescription = msg
            _uiState.value = SignInEvent.Error
        }
    }

    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            session.storeToken(token)
            _uiState.value = SignInEvent.Success
            _navigationEvent.emit(SignInNavigationEvent.NavigateToHome)
        }
    }

}