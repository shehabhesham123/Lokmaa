package com.example.lokma.pojo.listener

import com.example.lokma.pojo.model.Food
import java.io.Serializable

interface MenuListener:Serializable {
    fun setOnAddFoodToCart(food: Food)
}