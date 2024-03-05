package com.example.lokma.pojo

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.lokma.constant.Const


private const val ITEMS_SIZE = "SIZE"
private const val FOOD_NAME = "NAME"
private const val FOOD_ID = "ID"
private const val FOOD_IMAGE = "IMAGE"
private const val FOOD_PRICE = "PRICE"
private const val FOOD_TYPE = "TYPE"
private const val FOOD_RATING = "RATING"
private const val QUANTITY = "QUANTITY"


class Cart(private val restaurantId: String, val items: MutableList<OrderItem>) {

    fun removeThisItem(foodId: String) {
        for (i in items) {
            if (i.meal.id == foodId) {
                items.remove(i)
                break
            }
        }
    }

    fun storeDataAtLocalFile(context: Context) {
        val sharedPreferences =
            context.getSharedPreferences(Const.sharedPreferencesName, Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()

        Log.i("Cart", "${this.items.size}")
        edit.putInt("${restaurantId}/$ITEMS_SIZE", items.size)
        for ((id, i) in items.withIndex()) {
            val food = i.meal
            edit.putString("$id/$restaurantId$FOOD_ID", food.id)
            Log.i("aaaId1", "$id/$restaurantId$FOOD_ID")
            edit.putString("$id/$restaurantId$FOOD_NAME", food.name)
            edit.putString("$id/$restaurantId$FOOD_IMAGE", food.image)
            edit.putFloat("$id/$restaurantId$FOOD_PRICE", i.type.price)
            edit.putString("$id/$restaurantId$FOOD_TYPE", i.type.name)
            edit.putInt("$id/$restaurantId$FOOD_RATING", food.rating)
            edit.putInt("$id/$restaurantId$QUANTITY", i.quantity)
        }
        edit.apply()
        Log.i(
            "Cart",
            "${restaurantId}/$ITEMS_SIZE  ${
                sharedPreferences.getInt(
                    "${restaurantId}/$ITEMS_SIZE",
                    -1
                )
            }"
        )
    }

    fun deleteMyItems(context: Context) {
        val sharedPreferences =
            context.getSharedPreferences(Const.sharedPreferencesName, Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        edit.putInt("${restaurantId}/$ITEMS_SIZE", 0)
        edit.apply()
    }

    companion object {
        fun restoreDataFromLocalFile(
            restaurantId: String,
            context: Context
        ): MutableList<OrderItem> {

            val cartItems = mutableListOf<OrderItem>()
            val sharedPreferences =
                context.getSharedPreferences(Const.sharedPreferencesName, Context.MODE_PRIVATE)

            val size = sharedPreferences.getInt("${restaurantId}/$ITEMS_SIZE", 0)
            for (i in 0 until size) {
                val id = sharedPreferences.getString("$i/$restaurantId$FOOD_ID", null)
                val name = sharedPreferences.getString("$i/$restaurantId$FOOD_NAME", "")
                val imageUrl =
                    Uri.parse(sharedPreferences.getString("$i/$restaurantId$FOOD_IMAGE", null))
                val price = sharedPreferences.getFloat("$i/$restaurantId$FOOD_PRICE", 0f)
                val rating = sharedPreferences.getInt("$i/$restaurantId$FOOD_RATING", 0)
                val quantity = sharedPreferences.getInt("$i/$restaurantId$QUANTITY", 0)
                val type = sharedPreferences.getString("$id/$restaurantId$FOOD_TYPE", "")
                id?.let {
                    val item = OrderItem(
                        Meal(id, name!!, imageUrl.toString(), rating, mutableListOf()),
                        quantity,
                        Meal.Type(type!!, price)
                    )
                    cartItems.add(item)
                }
            }
            return cartItems
        }
    }
}
