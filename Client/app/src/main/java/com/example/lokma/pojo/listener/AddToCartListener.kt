package com.example.lokma.pojo.listener

import com.example.lokma.pojo.model.Item

interface AddToCartListener {
    fun setOnClickOnAddToCart(item: Item)
}