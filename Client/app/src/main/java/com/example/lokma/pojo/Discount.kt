package com.example.lokma.pojo

data class Discount(val meal: Meal,val percentage:Float){
    companion object{
        fun fromObjToMap(discount: Discount):Map<String,Any>{
            val data = mutableMapOf<String,Any>()
            data["FoodId"] = discount.meal.id!!
            data["Percentage"] = discount.percentage
            return data
        }
    }
}