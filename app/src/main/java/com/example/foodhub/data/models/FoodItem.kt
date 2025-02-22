package com.example.foodhub.data.models

import kotlinx.serialization.Serializable

@Serializable
data class FoodItem(
    val arModelUrl: String?,
    val createdAt: String,
    val description: String,
    val id: String,
    val image_url: String,
    val name: String,
    val price: String,
//    val restaurants: List<RestauranItem>,
)

@Serializable
data class RestauranItem(
    val id: String,
    val name: String,
    val address: String,
    val latitude: String,
    val longitude: String,
)
