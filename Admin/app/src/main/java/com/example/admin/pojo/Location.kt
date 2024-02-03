package  com.example.admin.pojo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlin.properties.Delegates

/**
 * make sure that you request permissions required
 */
open class Location {

    var latitude by Delegates.notNull<Double>()
        private set
    var longitude by Delegates.notNull<Double>()
        private set

    constructor(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    // this empty constructor is used by firebase when convert data to Location obj
    constructor()

    override fun toString(): String {
        return "Location [latitude: $latitude,   longitude: $longitude]"
    }

    companion object {
        @SuppressLint("MissingPermission")
        // this fun is static because it belongs to class no object
                /**
                 * this fun is used to get current location by gps
                 */
        fun getCurrentLocation(context: Context, onSuccess: (location: Location) -> Unit) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            if (permissionIsGranted(context)) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        onSuccess(Location(it.latitude, it.longitude))
                    }
            }
        }

        private fun permissionIsGranted(context: Context): Boolean {
            return (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        }
    }

}
