package com.example.admin.utils

import android.util.Log
import com.example.admin.pojo.Address
import com.example.admin.pojo.Admin
import com.example.admin.pojo.Meal
import com.example.admin.pojo.Order
import com.example.admin.pojo.Restaurant

class TempStorage {
    var restaurantName: String? = null
    var restaurantLogo: String? = null
    var restaurantAddress: Address? = null
    var order: Order? = null
    var restaurant: Restaurant? = null
    var admin: Admin? = null
    var meal: Meal? = null

    companion object {
        private var myInstance: TempStorage? = null
        fun instance(): TempStorage {
            if (myInstance == null) {
                myInstance = TempStorage()
            }
            return myInstance!!
        }
    }

    fun print(){
        Log.i("TempStorage","restaurantName : ${restaurantName}")
        Log.i("TempStorage","restaurantLogo:  ${restaurantLogo}")
        Log.i("TempStorage","restaurantAddress:  ${restaurantAddress}")
        Log.i("TempStorage","order:  ${order?.id}")
        Log.i("TempStorage","restaurant:   ${restaurant?.name}")
        Log.i("TempStorage","admin:   ${admin?.username}")
        Log.i("TempStorage","meal:   ${meal?.name}")
    }
}