package com.example.lokma.pojo.listener

import com.example.lokma.pojo.model.Cart
import com.example.lokma.pojo.model.Item

interface CartListener2 {
    fun setOnClickOnChangeLocation()
    fun restore(): MutableList<Item>
}