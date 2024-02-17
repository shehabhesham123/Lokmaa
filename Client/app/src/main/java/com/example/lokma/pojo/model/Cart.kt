package com.example.lokma.pojo.model

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.lokma.pojo.constant.Constant
import java.io.Serializable


private const val ITEMS_SIZE = "SIZE"
private const val FOOD_NAME = "NAME"
private const val FOOD_ID = "ID"
private const val FOOD_IMAGE = "IMAGE"
private const val FOOD_PRICE = "PRICE"
private const val FOOD_RATING = "RATING"
private const val QUANTITY = "QUANTITY"

class Cart(private val restaurantId: String, val items: MutableList<Item>) : Serializable {

    fun removeThisItem(foodId: String) {
        for (i in items) {
            if (i.food.id == foodId) {
                items.remove(i)
                break
            }
        }
    }

    fun storeDataAtLocalFile(context: Context) {
        val sharedPreferences =
            context.getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()

        Log.i("Cart","${this.items.size}")
        edit.putInt("${restaurantId}/$ITEMS_SIZE", items.size)
        for ((id, i) in items.withIndex()) {
            val food = i.food
            edit.putString("$id/$restaurantId$FOOD_ID", food.id)
            Log.i("aaaId1","$id/$restaurantId$FOOD_ID")
            edit.putString("$id/$restaurantId$FOOD_NAME", food.name)
            edit.putString("$id/$restaurantId$FOOD_IMAGE", food.imageUrl.toString())
            edit.putFloat("$id/$restaurantId$FOOD_PRICE", food.price.toFloat())
            edit.putFloat("$id/$restaurantId$FOOD_RATING", food.rating)
            edit.putInt("$id/$restaurantId$QUANTITY", i.quantity)
        }
        edit.apply()
        Log.i("Cart","${restaurantId}/$ITEMS_SIZE  ${sharedPreferences.getInt("${restaurantId}/$ITEMS_SIZE",-1)}")
    }

    fun deleteMyItems(context: Context) {
        val sharedPreferences =
            context.getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        edit.putInt("${restaurantId}/$ITEMS_SIZE", 0)
        edit.apply()
    }

    companion object {
        fun restoreDataFromLocalFile(restaurantId: String, context: Context): MutableList<Item> {

            val cartItems = mutableListOf<Item>()
            val sharedPreferences =
                context.getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE)

            val size = sharedPreferences.getInt("${restaurantId}/$ITEMS_SIZE", 0)
            for (i in 0 until size) {
                val id = sharedPreferences.getString("$i/$restaurantId$FOOD_ID", null)
                val name = sharedPreferences.getString("$i/$restaurantId$FOOD_NAME", "")
                val imageUrl =
                    Uri.parse(sharedPreferences.getString("$i/$restaurantId$FOOD_IMAGE", null))
                val price = sharedPreferences.getFloat("$i/$restaurantId$FOOD_PRICE", 0f)
                val rating = sharedPreferences.getFloat("$i/$restaurantId$FOOD_RATING", 0f)
                val quantity = sharedPreferences.getInt("$i/$restaurantId$QUANTITY", 0)
                id?.let {
                    val item = Item(Food(id, name!!, imageUrl, price, rating, mutableListOf(),
                        mutableListOf()), quantity)
                    cartItems.add(item)
                }
            }
            return cartItems
        }
    }
}

class Item(val food: Food, var quantity: Int) : Serializable {
    fun decreaseQuantity(): Int {
        return --quantity
    }

    fun increaseQuantity(): Int {
        return ++quantity
    }
}
