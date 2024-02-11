package com.example.admin.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.admin.backend.firebase.Firestore
import com.example.admin.pojo.Restaurant
import com.example.admin.utils.Const
import com.example.admin.utils.TempStorage

class RestaurantViewModel : ViewModel() {

    var restaurant = MutableLiveData<Restaurant>()
        private set

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
                    restaurant.value = it[0].toObject(Restaurant::class.java)
                }
            }, {}
        )
    }
}