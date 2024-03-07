package com.example.lokma.ui

import android.content.Context
import com.example.lokma.constant.Const
import com.example.lokma.network.firebase.Firestore
import com.example.lokma.pojo.Delivery

class DeliveryPresenter {
    companion object {
        fun getBestOne(context: Context, listener: DeliveryListener) {
            getDelivery(context, listener)
        }

        private fun getDelivery(context: Context, listener: DeliveryListener) {
            val firestore = Firestore(context)
            firestore.download(Const.DELIVERY_PATH, {
                if (it.isNotEmpty()) {
                    listener.onDeliveryReady(it[0].toObject(Delivery::class.java))
                }
            }, {})
        }
    }
}

interface DeliveryListener {
    fun onDeliveryReady(delivery: Delivery)
}