package com.example.lokma.pojo.model

import java.io.Serializable

data class User(
    val name: String?,
    val email: String,
    val password: String?,
    var address: Location?
) : Serializable {
    companion object {
        fun fromMapToObj(email: String, map: Map<String, Any>): User {
            val name = map["Name"].toString()
            val data = map["Address"] as? Map<*, *>
            var address: Location? = null
            data?.let {
                val lat = data["Lat"].toString().toDouble()
                val long = data["Long"].toString().toDouble()
                address = Location(lat, long)
            }
            return User(name, email, null, address)
        }
    }
}

