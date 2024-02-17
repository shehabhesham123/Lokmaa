package com.example.lokma.pojo.listener

import com.example.lokma.pojo.model.Restaurant

interface RestaurantListener {
    fun setOnClickOnRestaurant(restaurant: Restaurant)
}