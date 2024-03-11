package com.example.lokma.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.admin.GoogleMap
import com.example.lokma.R
import com.example.lokma.constant.TempStorage
import com.example.lokma.databinding.ActivityLocationBinding
import com.example.lokma.pojo.Address
import com.example.lokma.pojo.Location
import com.google.android.gms.maps.model.LatLng

class LocationActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityLocationBinding
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var register: ActivityResultLauncher<String>
    private var client = TempStorage.instance().client!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        mGoogleMap = GoogleMap()
        register = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            checkLocationPermission()
        }


        checkLocationPermission()

        mBinding.LocationFragmentButtonRequirePermission.setOnClickListener {
            register.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            register.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        mBinding.LocationFragmentButtonChangeLocation.setOnClickListener {
            changeLocation()
            mBinding.LocationFragmentButtonChangeLocation.isEnabled = false
        }

        mBinding.LocationFragmentButtonConfirmation.setOnClickListener {
            finish()
        }


    }

    val s = SeparatedThread()

    override fun onDestroy() {
        super.onDestroy()
        s.quit()
    }

    private fun checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(
                baseContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                baseContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mBinding.LocationFragmentConstraintLayoutRequirePermission.visibility = View.GONE
            mBinding.LocationFragmentConstraintLayoutLocationLayout.visibility = View.VISIBLE
            confirmGoogleMap()
            s.start()
            Handler(s.looper).post {
                putLocation()
            }
        } else {
            mBinding.LocationFragmentConstraintLayoutRequirePermission.visibility = View.VISIBLE
            mBinding.LocationFragmentConstraintLayoutLocationLayout.visibility = View.GONE

        }
    }

    private fun putLocation() {
        client.address?.let {
            putLocationData(it)
            return
        }
        val defaultLocation = Location()
        Address.getAddress(baseContext, defaultLocation) {
            putLocationData(it)
        }

    }

    private fun putLocationData(address: Address) {
        Handler(Looper.getMainLooper()).post {
            mBinding.LocationFragmentTextViewLongAddress.text = address.address
            mGoogleMap.addMarker(
                GoogleMap.Marker(
                    LatLng(address.latitude, address.longitude),
                    null,
                    null
                )
            )
        }
    }

    private fun changeLocation() {
        Address.getAddress(baseContext) {
            TempStorage.instance().client!!.address = it
            updateUI2()
        }
    }

    private fun updateUI2() {
        Handler(Looper.getMainLooper()).post {
            mBinding.LocationFragmentButtonChangeLocation.isEnabled = true
            mBinding.LocationFragmentTextViewLongAddress.text =
                TempStorage.instance().client!!.address!!.address
            val address = TempStorage.instance().client!!.address
            mGoogleMap.addMarker(
                GoogleMap.Marker(
                    LatLng(address!!.latitude, address.longitude),
                    null,
                    null
                )
            )
        }
    }


    private fun confirmGoogleMap() {
        supportFragmentManager.beginTransaction()
            .add(R.id.LocationFragment_Fragment_GoogleMap, mGoogleMap)
            .commit()
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, LocationActivity::class.java)
        }
    }
}