package com.example.delivery.utils

class OrderState {
    companion object {
        const val ACCEPTED: Int = 0b0
        const val CANCELED: Int = 0b1
        const val PREPARE: Int = 0b10
        const val ARRIVED: Int = 0b11
        const val NOTIFICATION_RECEIVE_TO_DELIVERY: Int = 0b100
        const val NOTIFICATION_NOT_RECEIVE_TO_DELIVERY: Int = 0b101
    }
}