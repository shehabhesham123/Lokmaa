package com.example.lokma.pojo.model

import android.location.Location
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Location(var latitude: Double, var longitude: Double) {

    companion object {
        val defaultLocation: Location = Location("")
            get() {
                field.latitude = 27.185894
                field.longitude = 31.168571
                return field
            }

        fun distance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
            val r = 6371;
            val dLat = Math.toRadians(lat2 - lat1)
            val dLong = Math.toRadians(long2 - long1)
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLong / 2) * sin(dLong / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return r * c
        }
    }

    fun getLocation(): Location {
        val location = Location("")
        location.longitude = longitude
        location.latitude = latitude
        return location
    }

    data class Address(
        val shortAddress: String, val longAddress: String
    )
}