package com.example.lokma.constant
class Const {
    companion object {
        const val ADMIN_PATH = "Admins"
        const val CLIENT_PATH = "Clients"
        const val RESTAURANT_PATH = "Restaurants"
        const val LOGO_PATH = "logos"
        const val MEAL_IMAGE_PATH = "meals"
        fun ordersPath (resId:String) ="$RESTAURANT_PATH/$resId/Orders"

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