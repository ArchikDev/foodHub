package com.example.foodhub.data

import com.example.foodhub.data.models.AuthResponse
import com.example.foodhub.data.models.Category
import com.example.foodhub.data.models.FoodItemResponse
import com.example.foodhub.data.models.Restaurant
import com.example.foodhub.data.models.SignInRequest
import com.example.foodhub.data.models.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FoodApi {
    @GET("category")
    suspend fun getCategories(): Response<List<Category>>

    @GET("restaurants")
    suspend fun getRestaurants(): Response<List<Restaurant>>

    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>

    @POST("user/login")
    suspend fun signIn(@Body request: SignInRequest): Response<AuthResponse>

    @GET("menu-Items/{restaurantId}/menu")
    suspend fun getFoodItemRestaurant(@Path("restaurantId") restaurantId: String): Response<FoodItemResponse>

//    @POST("/auth/oauth")
//    suspend fun oAuth(@Body request: OAuthRequest): Response<AuthResponse>
}