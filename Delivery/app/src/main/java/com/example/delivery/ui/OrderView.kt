package com.example.delivery.ui

import com.example.delivery.pojo.Order

interface OrderView {
    fun onGetOrders(orders: MutableList<Order>)
}