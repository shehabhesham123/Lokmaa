package com.example.lokma.pojo.model

import android.net.Uri
import android.util.Log

class Food(
    val id: String,
    var name: String,
    val imageUrl: Uri,
    var price: Float,
    val rating: Float,
    val sauce: List<Adding>,
    val addTopping: List<Adding>
) {
    companion object {
        fun fromMapToObj(
            id: String,
            data: Map<String, Any>
        ): Pair<Food, String> {  // pair.second --> category id
            val name = data["Name"].toString()
            val imageUrl = Uri.parse(data["ImageUrl"].toString())
            val price = data["Price"].toString().toFloat()
            val rating = data["Rating"].toString().toFloat()
            val categoryId = data["CategoryId"].toString()
            val sauces = mutableListOf<Adding>()
            val sauceList = data["Sauce"] as? List<*>
            sauceList?.let {
                for (i in sauceList) {
                    val sauceData = i as Map<*, *>
                    val sauceName = sauceData["Name"].toString()
                    val saucePrice = sauceData["Price"].toString().toFloat()
                    sauces.add(Adding(sauceName, saucePrice,false))
                }
            }
            val addTopping = mutableListOf<Adding>()
            val addToppingList = data["AddTopping"] as? List<*>
            addToppingList?.let {
               // Log.i("aaaa1","${addToppingList.size}")
                for (i in addToppingList) {
                    val addToppingData = i as Map<String, Any>
                    val addToppingName = addToppingData["Name"].toString()
                    val addToppingPrice = addToppingData["Price"].toString().toFloat()
                    addTopping.add(Adding(addToppingName, addToppingPrice,false))
                }
            }
            return Pair(Food(id, name, imageUrl, price, rating, sauces, addTopping), categoryId)
        }
    }

    fun fromObjToMap(categoryId: String, food: Food): Map<String, Any> {
        val data = mutableMapOf<String, Any>()
        data["CategoryId"] = categoryId
        data["Name"] = food.name
        data["ImageUrl"] = food.imageUrl.toString()
        data["Price"] = food.price
        data["Rating"] = food.rating
        data["Sauce"] = food.sauce
        data["AddTopping"] = food.addTopping
        return data
    }

    class Adding(val name: String, val price: Float,var isChecked: Boolean)
}
