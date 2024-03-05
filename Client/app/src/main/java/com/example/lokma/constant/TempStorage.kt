package com.example.lokma.constant

import com.example.lokma.pojo.Restaurant


class TempStorage {
    var restaurants: MutableList<Restaurant> = mutableListOf()
    var currentRestaurant: Restaurant? = null

    companion object {
        private var myInstance = TempStorage()
        fun instance(): TempStorage {
            return myInstance
        }
    }
}