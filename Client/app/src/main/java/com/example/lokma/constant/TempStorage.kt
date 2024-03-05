package com.example.lokma.constant

import com.example.lokma.pojo.Meal
import com.example.lokma.pojo.OrderItem
import com.example.lokma.pojo.Restaurant


class TempStorage {
    var restaurants: MutableList<Restaurant> = mutableListOf()
    var currentRestaurant: Restaurant? = null
    var meal: Meal? = null
    var orderItem:OrderItem? = null

    companion object {
        private var myInstance = TempStorage()
        fun instance(): TempStorage {
            return myInstance
        }
    }
}