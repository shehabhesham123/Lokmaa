package com.example.delivery.utils

abstract class Rating {
    companion object {
        /** this rating will use to rate the restaurants and meals */
        const val BAD: Int = 0b00
        const val NORMAL: Int = 0b01
        const val GOOD: Int = 0b10
        const val VERY_GOOD: Int = 0b11
    }
}