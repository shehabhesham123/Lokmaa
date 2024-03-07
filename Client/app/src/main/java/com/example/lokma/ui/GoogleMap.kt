package com.example.admin

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.lokma.R
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
        googleMap.clear()
        val marker = MarkerOptions()
            .position(markerParam.latLng)
            .title(markerParam.title)
        if (markerParam.icon != null) {
            val mIcon = BitmapDescriptorFactory.fromBitmap(markerParam.icon!!)
            marker.icon(mIcon)
        }

        // must use addMarker in main looper
        Handler(Looper.getMainLooper()).post {
            googleMap.addMarker(marker)
            newMarkerIsAdded(markerParam.latLng)
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
    fun moveCamera(vararg latLng: LatLng) {
        val latLongBoundBuilder = LatLngBounds.builder()
        for (i in latLng) {
            latLongBoundBuilder.include(i)
        }
        val latLongBound = latLongBoundBuilder.build()
        val margin = resources.getDimensionPixelSize(R.dimen.map_margin)
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLongBound, margin)
        googleMap.animateCamera(cameraUpdate)
    }

    class Marker(latLng: LatLng, icon: Bitmap?, title: String?) {
        var latLng = latLng
            private set
        var icon = icon
            private set
        var title = title
            private set

    }
}