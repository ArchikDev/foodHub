package com.example.foodhub.ui.features.auth.signup

import androidx.lifecycle.viewModelScope
import com.example.foodhub.FoodHubSession
import com.example.foodhub.data.FoodApi
import com.example.foodhub.data.models.SignUpRequest
import com.example.foodhub.data.remote.ApiResponse
import com.example.foodhub.data.remote.safeApiCall
import com.example.foodhub.ui.features.auth.AuthScreenViewModel.AuthEvent
import com.example.foodhub.ui.features.auth.BaseAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    override val foodApi: FoodApi,
    val session: FoodHubSession
): BaseAuthViewModel(foodApi) {

    private val _uiState = MutableStateFlow<SignUpEvent>(SignUpEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignUpNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _uiState.value = SignUpEvent.Loading

            try {
                val response = safeApiCall {
                    foodApi.signUp(
                        SignUpRequest(
                            name = name.value,
                            email = email.value,
                            password = password.value
                        )
                    )
                }

                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.value = SignUpEvent.Success
                        session.storeToken(response.data.token)
                        _navigationEvent.emit(SignUpNavigationEvent.NavigateToHome)
                    }
                    else -> {
                        val err = (response as? ApiResponse.Error)?.code ?: 0
                        error = "Sign In Failed"
                        errorDescription = "Failed to sign up"
                        _uiState.value = SignUpEvent.Error

                        when (err) {
                            400 -> {
                                error = "Invalid Credintials"
                                errorDescription = "Please enter correct details"
                            }
                        }
                    }
                }


            } catch (e: Exception) {
                _uiState.value = SignUpEvent.Error
            }
        }

    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SignUpNavigationEvent.NavigateToLogin)
        }
    }

    sealed class SignUpNavigationEvent {
        data object NavigateToLogin: SignUpNavigationEvent()
        data object NavigateToHome: SignUpNavigationEvent()
    }

    sealed class SignUpEvent {
        data object Nothing: SignUpEvent()
        data object Success: SignUpEvent()
        data object Error: SignUpEvent()
        data object Loading: SignUpEvent()
    }

    override fun loading() {
        viewModelScope.launch {
            _uiState.value = SignUpEvent.Loading
        }
    }

    override fun onGoogleError(msg: String) {
        viewModelScope.launch {
            error = "Google Sign Up Failed"
            errorDescription = msg
            _uiState.value = SignUpEvent.Error
        }
    }


    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            session.storeToken(token)
            _uiState.value = SignUpEvent.Success
            _navigationEvent.emit(SignUpNavigationEvent.NavigateToHome)
        }
    }
}