package com.example.admin.utils

class Const {
    companion object {
        const val ADMIN_PATH = "Admins"
        const val RESTAURANT_PATH = "Restaurants"
        const val LOGO_PATH = "logos"
        const val MEAL_IMAGE_PATH = "meals"
        fun ordersPath (resId:String) ="$RESTAURANT_PATH/$resId/Orders"
    }
}