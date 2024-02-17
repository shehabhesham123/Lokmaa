package com.example.delivery.utils

import com.example.delivery.pojo.Delivery
import com.example.delivery.pojo.Order

class TempStorage {

    var delivery: Delivery? = null
    var order: Order? = null

    companion object {
        private val instance = TempStorage()
        fun instance(): TempStorage {
            return instance
        }
    }
}