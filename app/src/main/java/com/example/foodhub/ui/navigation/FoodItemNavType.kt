package com.example.foodhub.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.example.foodhub.data.models.FoodItem
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

val foodItemNavType = object : NavType<FoodItem>(false) {
    override fun parseValue(value: String): FoodItem {
        return Json.decodeFromString(FoodItem.serializer(), value)
    }

    override fun get(bundle: Bundle, key: String): FoodItem? {
        return parseValue(bundle.getString(key).toString()).copy(
            image_url = URLDecoder.decode(
                parseValue(bundle.getString(key).toString()).image_url,
                "UTF-8"
            )
        )
    }

    override fun serializeAsValue(value: FoodItem): String {
        return Json.encodeToString(FoodItem.serializer(), value.copy(
            image_url = URLEncoder.encode(value.image_url, "UTF-8")

        ))
    }

    override fun put(bundle: Bundle, key: String, value: FoodItem) {
        bundle.putString(key, serializeAsValue(value))
    }
}