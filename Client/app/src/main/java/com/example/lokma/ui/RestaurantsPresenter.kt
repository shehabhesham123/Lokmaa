package com.example.lokma.ui

import android.content.Context
import android.widget.Toast
import com.example.lokma.constant.Const.Companion.RESTAURANT_PATH
import com.example.lokma.network.firebase.Firestore
import com.example.lokma.pojo.Restaurant

class RestaurantsPresenter {
    companion object {
        fun getRestaurants(context: Context, listener: RestaurantListener) {
            getRestaurantsFromDatabase(context, listener)
        }

        private fun getRestaurantsFromDatabase(context: Context, listener: RestaurantListener) {
            val firestore = Firestore(context)
            firestore.download(RESTAURANT_PATH, {
                val restaurants = mutableListOf<Restaurant>()
                for (i in it) {
                    val res = i.toObject(Restaurant::class.java)
                    restaurants.add(res)
                }
                listener.setOnRestaurantsIsReady(restaurants)
            }, {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            })
        }
    }
}

interface RestaurantListener {
    fun setOnRestaurantsIsReady(restaurants: MutableList<Restaurant>)
}