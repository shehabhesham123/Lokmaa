package com.example.admin.utils

class TempStorage {
    var restaurantName: String? = null
    var restaurantLogo: String? = null

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