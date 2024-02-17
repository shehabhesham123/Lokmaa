package com.example.lokma.pojo.model

import android.net.Uri
import com.example.lokma.R
import java.io.Serializable

class Restaurant(
    val id: String,
    logo: Uri,
    name: String,
    type: String,
    rating: Float,
    avgDeliveryTime: Float,
    avgDeliveryCost: Float,
    val lat: Double,
    val long: Double
) : Serializable {

    val logo: Uri = Validation.imageIsValid(logo)
    val name: String = Validation.stringIsValid(name)
    val type: String = Validation.stringIsValid(type)
    val rating: Rating
    val avgDeliveryTime: String  // "00:15"
    val avgDeliveryCost: Float    //4.99

    init {
        this.rating = rating(Validation.ratingIsValid(rating))
        this.avgDeliveryTime = time(Validation.timeIsValid(avgDeliveryTime))
        this.avgDeliveryCost = Validation.costIsValid(avgDeliveryCost)
    }

    class Rating(val icon: Int, val name: String)

    private fun rating(rating: Float): Rating {
        return when {
            rating >= 4.0 -> Rating(R.drawable.ic_rating_amazing, "Amazing")
            rating >= 3.0 -> Rating(R.drawable.ic_rating_very_good, "Very good")
            rating >= 2.0 -> Rating(R.drawable.ic_rating_good, "Good")
            else -> Rating(R.drawable.ic_rating_ok, "Ok")
        }
    }

    private fun time(time: Float): String {
        val hour = (time / 60).toInt()
        val mins = (time % 60).toInt()
        return "${String.format("%02d", hour)}:${String.format("%02d", mins)}"
    }

    fun avgDeliveryCost(): String {
        return if (this.avgDeliveryCost == 0.0f) "Free"
        else "${this.avgDeliveryCost} EGP"
    }

    companion object {
        fun fromMapToObj(id: String, data: Map<String, Any>): Restaurant {
            val name = data["Name"].toString()
            val avgDeliveryCost = data["DeliveryCost"].toString().toFloat()
            val avgDeliveryTime = data["DeliveryTime"].toString().toFloat()
            val lat = data["Lat"].toString().toDouble()
            val long = data["Long"].toString().toDouble()
            val rating = data["Rating"].toString().toFloat()
            val type = data["Type"].toString()
            val logo = Uri.parse(data["LogoUrl"].toString())
            return Restaurant(
                id,
                logo,
                name,
                type,
                rating,
                avgDeliveryTime,
                avgDeliveryCost,
                lat,
                long
            )
        }

        fun fromObjToMap(restaurant: Restaurant): Map<String, Any> {
            val data = mutableMapOf<String, Any>()
            data["Name"] = restaurant.name
            data["DeliveryCost"] = restaurant.avgDeliveryCost
            data["DeliveryTime"] = restaurant.avgDeliveryTime
            data["Lat"] = restaurant.lat
            data["Long"] = restaurant.long
            data["Rating"] = restaurant.rating
            data["Type"] = restaurant.type
            data["LogoUrl"] = restaurant.logo.toString()
            return data
        }
    }

}