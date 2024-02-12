package com.example.admin.ui

import com.example.admin.pojo.Restaurant

interface RestaurantView {
    fun onGetRestaurant(restaurant: Restaurant)
}