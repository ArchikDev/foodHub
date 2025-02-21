package com.example.foodhub.data.models

import kotlinx.serialization.Serializable

data class Restaurant(
    val id: String,
    val name: String,
    val address: String,
    val image_url: String,
    val latitude: String,
    val longitude: String,
    val categories: List<CategoryItem>,
)

@Serializable
data class CategoryItem(
    val name: String
)

//id": 10,
//"name": "Pizza Palace",
//"address": "",
//"image_url": "http://localhost:5000/c905db1b-c7ef-4adf-b258-3093c9580506.png",
//"latitude": "40.712776",
//"longitude": "-74.005978",
//"createdAt": "2025-02-16T16:20:24.304Z",
//"updatedAt": "2025-02-16T16:20:24.304Z",
//"categories": [
//{
//    "id": 6,
//    "name": "Pizza"
//}
//],
//"user": {
//    "id": 1,
//    "email": "user@mail.ru",
//    "role": "ADMIN"
//}