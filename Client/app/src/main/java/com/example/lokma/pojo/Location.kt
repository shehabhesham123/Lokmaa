package com.example.lokma.pojo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * make sure that you request permissions required
 */
open class Location {

    var latitude: Double = 27.185894
        private set
    var longitude: Double = 31.168571
        private set

    constructor(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    // this empty constructor is used by firebase when convert data to Location obj
    constructor()

    override fun toString(): String {
        return "Location [latitude: $latitude,   longitude: $longitude]"
    }

    companion object {
        @SuppressLint("MissingPermission")
        // this fun is static because it belongs to class no object
                /**
                 * this fun is used to get current location by gps
                 */
        fun getCurrentLocation(context: Context, onSuccess: (location: Location) -> Unit) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            Log.i("shehab","${permissionIsGranted(context)}")
            if (permissionIsGranted(context)) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        onSuccess(Location(it.latitude, it.longitude))
                    }
            }
        }

        private fun permissionIsGranted(context: Context): Boolean {
            return (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
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




}
