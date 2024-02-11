package com.example.admin.utils

import com.example.admin.pojo.Address
import com.example.admin.pojo.Admin
import com.example.admin.pojo.Order
import com.example.admin.pojo.Restaurant

class TempStorage {
    var restaurantName: String? = null
    var restaurantLogo: String? = null
    var restaurantAddress: Address? = null
    var order: Order? = null
    var restaurant: Restaurant? = null
    var admin: Admin? = null

    companion object {
        private var myInstance: TempStorage? = null
        fun instance(): TempStorage {
            if (myInstance == null) {
                myInstance = TempStorage()
            }
            return myInstance!!
        }
    }
}