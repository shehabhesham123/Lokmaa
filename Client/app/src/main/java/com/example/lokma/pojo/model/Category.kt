package com.example.lokma.pojo.model

import java.io.Serializable

class Category(val id: String,val name: String,val foods: MutableList<Food>):Serializable {

    fun fromObjToMap():Map<String,Any>{
        val data = mutableMapOf<String,Any>()
        data["CategoryName"] = this.name
        return data
    }

    companion object {
        fun fromMapToObj(id: String, data: Map<String, Any>): Category {
            val name = data["CategoryName"].toString()
            return Category(id, name, mutableListOf())
        }
    }
}