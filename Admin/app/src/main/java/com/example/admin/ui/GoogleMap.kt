package com.example.admin.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.admin.R
import com.example.admin.pojo.Address
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

/**
 * dependencies
 *
 * - implementation("com.google.android.gms:play-services-location:21.1.0")
 *
 * - implementation("com.google.android.gms:play-services-maps:18.2.0")
 *
 * in manifest
 *
 * - meta-data  android:name="com.google.android.geo.API_KEY" android:value="API_KEY"
 * - (ACCESS_NETWORK_STATE) permissions
 *
 * you can use MapFragment or SupportMapFragment to host mapView.
 *  supportMapFragment is better to support old android version
 *  supportMapFragment has own onCreateView()
 */
class GoogleMap : SupportMapFragment() {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // it gets a map object asynchronously.
        // and call it in onCreate, so you will get a reference to a GoogleMap once it is created and initialized.
        getMapAsync {
            googleMap = it
            googleMap.uiSettings.isZoomControlsEnabled = true
        }
    }

    /**
     * this fun is used to add mark to address on map
     */
    fun addMarker(markerParam: Marker) {
        val address = markerParam.address
        val latLng = LatLng(address.latitude, address.longitude)
        val marker = MarkerOptions()
            .position(latLng)
            .title(markerParam.title)
        if (marker.icon != null) {
            val mIcon = BitmapDescriptorFactory.fromBitmap(markerParam.icon!!)
            marker.icon(mIcon)
        }

        // must use addMarker in main looper
        Handler(Looper.getMainLooper()).post {
            googleMap.addMarker(marker)
            newMarkerIsAdded(latLng)
        }
    }

    /**
     * this fun is used to delete all markers that are on map
     */
    fun deleteMarkers() {
        googleMap.clear()
    }

    /**
     * this fun is used to add this marker in mapMarkers and move camera
     */
    private fun newMarkerIsAdded(latLng: LatLng) {
        moveCamera(latLng)
    }

    /**
     * this fun is used to move camera to a marker on the map and zoom in
     */
    private fun moveCamera(latLng: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
        googleMap.animateCamera(cameraUpdate)
    }

    /**
     * this fun is used to move camera to appear all addresses on the map.
     * but can't make zoom in or out in google map
     */
    fun moveCamera(vararg addresses: Address) {
        val latLongBoundBuilder = LatLngBounds.builder()
        for (i in addresses) {
            latLongBoundBuilder.include(LatLng(i.latitude, i.longitude))
        }
        val latLongBound = latLongBoundBuilder.build()
        val margin = resources.getDimensionPixelSize(R.dimen.map_margin)
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLongBound, margin)
        googleMap.animateCamera(cameraUpdate)
    }

    class Marker(address: Address, icon: Bitmap?, title: String?) {
        var address = address
            private set
        var icon = icon
            private set
        var title = title
            private set
    }
}