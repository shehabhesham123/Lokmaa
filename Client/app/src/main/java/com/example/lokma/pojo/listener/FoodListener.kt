package com.example.lokma.pojo.listener

import com.example.lokma.pojo.model.Food

interface FoodListener {
    fun setOnClickOnFood(food: Food)
}