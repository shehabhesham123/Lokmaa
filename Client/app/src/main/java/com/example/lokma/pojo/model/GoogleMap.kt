package com.example.lokma.pojo.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.lokma.pojo.constant.Constant
import com.example.lokma.pojo.model.Validation.Companion.isOnline
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject


// throw IllegalAccessException if Permission is denied (ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION)
class GoogleMap(
    private val context: Context,
    fragmentManager: FragmentManager,    // childFragmentManager
    googleMapFragmentId: Int
) {

    private val sharedPreferences =
        context.getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE)

    private var fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var networkInfo: NetworkInfo? = null

    private var map: GoogleMap? = null

    private val googleMapFragment: SupportMapFragment? =
        if (googleMapFragmentId != 0) fragmentManager.findFragmentById(googleMapFragmentId) as SupportMapFragment
        else null

    init {
        if (isOnline(context)) {
            googleMapFragment?.getMapAsync { map ->
                this.map = map
                if (checkPermission(context)) {
                    this.map?.isMyLocationEnabled = true
                    this.map?.uiSettings?.isMyLocationButtonEnabled = false
                }
            }
        } else {
            // no internet connection
            throw Exception("no internet connection")
        }
    }

    fun moveToLocation(
        location: android.location.Location,
        whatDoAfterDone: (Location.Address) -> Unit
    ) {
        if (this.map == null) {
            /* this mean that
                googleMapFragment.getMapAsync { map ->
                    .
                    .
                    .
               }
               doesn't finish yet
               so we will wait 500 ms then call it again
               why this fun ---> because i call it in location fragment at the first فى البداية
             */
            Handler().postDelayed({
                moveToLocation(location, whatDoAfterDone)
            }, 500)
        } else {
            if (isOnline(context)) {
                moveCamera(this.map, location)
                getAddress(location) {
                    whatDoAfterDone(it)
                }
            } else {
                // no internet connection
                throw Exception("no internet connection")
            }
        }
    }

    fun getCurrentLocation(whatDoAfterDone: (Location.Address, Location) -> Unit) {
        fetchLastLocation(whatDoAfterDone)
    }

    private fun fetchLastLocation(whatDoAfterDone: (Location.Address, Location) -> Unit) {

        // check permission
        if (checkPermission(context)) {

            if (isOnline(context)) {
                fusedLocationProviderClient.lastLocation
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val currentLocation = task.result
                            if (currentLocation != null) {
                                moveCamera(this.map, currentLocation)
                                getAddress(currentLocation) {
                                    whatDoAfterDone(
                                        it, Location(
                                            currentLocation.latitude,
                                            currentLocation.longitude
                                        )
                                    )
                                }
                            } else {
                                // currentLocation is null
                                throw Exception("currentLocation is null")
                            }
                        } else {
                            // task is not successful
                            throw Exception("task is not successful")
                        }
                    }
                    .addOnFailureListener {
                        // task fail
                        throw Exception("task fail")
                    }
            } else {
                throw Exception("No internet connection")
            }
        } else {
            throw IllegalAccessException("Permission is denied")
        }
    }

    private fun moveCamera(googleMap: GoogleMap?, currentLocation: android.location.Location) {
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    currentLocation.latitude,
                    currentLocation.longitude
                ), 18f
            )
        )
    }

    companion object {

        fun checkPermission(context: Context): Boolean {
            return (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    )
        }
    }

    fun getAddress(
        currentLocation: android.location.Location,
        whatDoAfterDone: (Location.Address) -> Unit
    ) {
        // open cage api
        // en for english   ... ar for arabic
        val url =
            "https://api.opencagedata.com/geocode/v1/json?q=${currentLocation.latitude}+${currentLocation.longitude}&key=97aa2a22eb6b4c48a447d98ac4baf3a1&language=en&pretty=1"
        // volley
        val stringRequest = StringRequest(Request.Method.GET, url, {
            val jsonObject = JSONObject(it)
            val results = jsonObject.getJSONArray("results")
            val components = results.getJSONObject(0).getJSONObject("components")
            val road = components.getString("road")
            val formatted = results.getJSONObject(0).getString("formatted")
            whatDoAfterDone(Location.Address(road, formatted))
        }, {
            Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
        })
        Singleton.getInstance(context).addRequest(stringRequest)
    }


}