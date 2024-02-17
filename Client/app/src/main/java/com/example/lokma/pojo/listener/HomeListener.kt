package com.example.lokma.pojo.listener

import com.example.lokma.pojo.model.Food

interface HomeListener {
    fun setOnClickOnLocation()
    fun setOnClickOnDiscount(food: Food)
    fun setOnClickOnFood(food: Food)
}