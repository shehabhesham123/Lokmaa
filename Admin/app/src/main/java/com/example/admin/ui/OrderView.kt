package com.example.admin.ui

import com.example.admin.pojo.Order

interface OrderView {
    fun onGetOrders(orders: MutableList<Order>)
}