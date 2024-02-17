package com.example.lokma.pojo.model

data class Discount(val food: Food,val percentage:Float){
    companion object{
        fun fromObjToMap(discount: Discount):Map<String,Any>{
            val data = mutableMapOf<String,Any>()
            data["FoodId"] = discount.food.id
            data["Percentage"] = discount.percentage
            return data
        }
    }
}