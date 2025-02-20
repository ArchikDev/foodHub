package com.example.foodhub.ui.features.auth

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.data.FoodApi
import com.example.foodhub.data.auth.GoogleAuthUiProvider
import com.example.foodhub.data.remote.ApiResponse
import com.example.foodhub.data.remote.safeApiCall
import kotlinx.coroutines.launch

abstract class BaseAuthViewModel(open val foodApi: FoodApi): ViewModel() {
    var error: String = ""
    var errorDescription = ""
    private val googleAuthUiProvider = GoogleAuthUiProvider()

    abstract fun loading()
    abstract fun onGoogleError(msg: String)
    abstract fun onSocialLoginSuccess(token: String)

    fun onGoogleClicked(context: ComponentActivity) {
        initiateGoogleLogin(context)
    }

    private fun initiateGoogleLogin(context: ComponentActivity) {
        viewModelScope.launch {
            loading()

            try {
                val response = googleAuthUiProvider.signIn(
                    context,
                    CredentialManager.create(context)
                )

//            fetchFoodApiToken(response.token, "google") {
//                onGoogleError(it)
//            }

                if (response != null) {
//                val request = OAuthRequest(
//                    token = response.token,
//                    provider = "google"
//                )
//
//                val res = foodApi.oAuth(request)

                    onSocialLoginSuccess("")

//                if (res.token.isNotEmpty()) {
//                    onSocialLoginSuccess(res.token)
//                } else {
//                    onGoogleError("Failed google auth")
//                }

                } else {
                    onGoogleError("Failed google auth")
                }
            } catch (e: Throwable) {
                onGoogleError(e.message.toString())
            }

        }
    }

    private fun fetchFoodApiToken(token: String, provider: String, onError: (String) -> Unit) {
        viewModelScope.launch {
//            val request = OAuthRequest(
//                token = response.token,
//                provider = "google"
//            )

//            val res = safeApiCall { foodApi.oAuth(request) }

//            when (res) {
//                is ApiResponse.Success -> {
//                    onSocialLoginSuccess(res.data.token)
//                }
//                else -> {
//                    val error = (res as? ApiResponse.Error)?.code
//
//                    if (error != null) {
//                        when (error) {
//                            401 -> onError("Invalid Token")
//                            500 -> onError("Server Error")
//                            404 -> onError("Not Found")
//                            else -> onError("Failed")
//                        }
//                    }
//                }
//            }
        }
    }

}