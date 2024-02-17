package com.example.lokma.pojo.constant

class Constant {
    companion object {
        const val sharedPreferencesName = "File"
        const val networkConnectionKey = "networkConnection"
        const val firstOpenKey = "firstOpen"
        const val restaurantsPath = "Restaurant"
        fun foodPath(restaurantId: String) = "Restaurant/$restaurantId/Food"
        fun categoryPath(restaurantId: String) = "Restaurant/$restaurantId/category"
        fun discountPath(restaurantId: String) = "Restaurant/$restaurantId/Discount"
        fun userPath(userEmail: String) = "Users/$userEmail"
        const val topFoodPath = "Rating"
        const val orderPath = "Orders"
    }
}