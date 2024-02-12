package com.example.admin.ui

import android.content.Context
import com.example.admin.backend.firebase.Firestore
import com.example.admin.pojo.Order
import com.example.admin.utils.Const

class OrdersPresenter(private val orderView: OrderView) {

    fun getOrders(context: Context, restaurantId: String) {
        getOrdersFromDatabase(context, restaurantId)
    }

    private fun getOrdersFromDatabase(context: Context, restaurantId: String) {
        val firestore = Firestore(context)
        firestore.download(Const.ordersPath(restaurantId), {
            val orders = mutableListOf<Order>()
            for (i in it) {
                orders.add(i.toObject(Order::class.java))
            }
            orderView.onGetOrders(orders)
        }, {

        })
    }
}