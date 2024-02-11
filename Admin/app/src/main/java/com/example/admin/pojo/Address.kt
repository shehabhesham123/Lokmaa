package com.example.admin.pojo

import android.content.Context
import android.os.Handler
import com.example.admin.backend.Network
import com.example.admin.ui.SeparatedThread
import org.json.JSONObject
import kotlin.properties.Delegates

class Address : Location {
    var address by Delegates.notNull<String>()
        private set

    /**
     * this constructor is used when create new address
     */
    constructor(latitude: Double, longitude: Double, address: String) : super(latitude, longitude) {
        this.address = address
    }

    /** this empty constructor is used by firebase when convert data to Address obj */
    constructor()

    override fun toString(): String {
        return "Address [latitude: $latitude, longitude: $longitude,   address: $address ]"
    }

    companion object {
        /**
         * this fun is used when create new address
         */
        fun getAddress(context: Context, onSuccess: (address: Address) -> Unit) {
            getCurrentLocation(context) {
                /*
                    when get current location lat,long, then get address geocoding
                    you must call geocodingJson() in separated thread not main thread
                */
                val separatedThread = SeparatedThread()
                separatedThread.start()
                Handler(separatedThread.looper).post {
                    val addressJson =
                        geocodingJson(context, Location(it.latitude, it.longitude))
                    val address =
                        addressJson.getJSONArray("results").getJSONObject(0).getString("formatted")
                    onSuccess(
                        Address(
                            it.latitude,
                            it.longitude,
                            address
                        )
                    )
                    separatedThread.quit()
                }
            }
        }

        private fun geocodingJson(context: Context, location: Location): JSONObject {
            val url =
                "https://api.opencagedata.com/geocode/v1/json?q=${location.latitude}%2C${location.longitude}&key=97aa2a22eb6b4c48a447d98ac4baf3a1"
            val network = Network()
            return network.getJsonBody(context, url)
        }
    }
}
