package com.example.delivery.ui

import android.content.Context
import com.example.delivery.backend.firebase.Firestore
import com.example.delivery.pojo.Order
import com.example.delivery.utils.Const

class OrdersPresenter(private val orderView: OrderView) {

    fun getOrders(context: Context, restaurantId: String) {
        getOrdersFromDatabase(context, restaurantId)
    }

    private fun getOrdersFromDatabase(context: Context, deliveryUsername: String) {
        val firestore = Firestore(context)
        firestore.download(Const.MY_ODERDER_PATH(deliveryUsername), {
            val orders = mutableListOf<Order>()
            for (i in it) {
                orders.add(i.toObject(Order::class.java))
            }
            orderView.onGetOrders(orders)
        }, {

        })
    }
}