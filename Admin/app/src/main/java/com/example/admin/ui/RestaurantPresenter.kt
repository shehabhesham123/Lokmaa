package com.example.admin.ui

import android.content.Context
import com.example.admin.backend.firebase.Firestore
import com.example.admin.pojo.Restaurant
import com.example.admin.utils.Const
import com.example.admin.utils.TempStorage

class RestaurantPresenter(private val restaurantView: RestaurantView) {

    fun getRestaurant(context: Context) {
        getRestaurantFromDatabase(context)
    }

    private fun getRestaurantFromDatabase(context: Context) {
        val firestore = Firestore(context)
        firestore.download(
            Const.RESTAURANT_PATH,
            "admin.username",
            TempStorage.instance().admin!!.username, {
                if (it.size == 1) {
                    val restaurant = it[0].toObject(Restaurant::class.java)
                    restaurantView.onGetRestaurant(restaurant)
                }
            }, {}
        )
    }
}