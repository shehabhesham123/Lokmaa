package com.example.delivery.utils

class Const {
    companion object {
        val DELIVERY_PATH = "Deliveries"
        fun MY_ODERDER_PATH(deliveryUsername:String) = "Deliveries/$deliveryUsername/Orders"
    }
}