package com.example.lokma.constant
class Const {
    companion object {
        const val ADMIN_PATH = "Admins"
        const val CLIENT_PATH = "Clients"
        const val RESTAURANT_PATH = "Restaurants"
        const val LOGO_PATH = "logos"
        const val MEAL_IMAGE_PATH = "meals"
        fun ordersPath (resId:String) ="$RESTAURANT_PATH/$resId/Orders"

        val DELIVERY_PATH = "Deliveries"
        fun MY_ODERDER_PATH(deliveryUsername:String) = "Deliveries/$deliveryUsername/Orders"

    }
}