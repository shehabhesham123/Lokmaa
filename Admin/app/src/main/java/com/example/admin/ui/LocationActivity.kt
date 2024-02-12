package com.example.admin.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.admin.GoogleMap
import com.example.admin.R
import com.example.admin.databinding.ActivityLocationBinding
import com.example.admin.pojo.Address
import com.example.admin.utils.TempStorage
import com.google.android.gms.maps.model.LatLng


class LocationActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityLocationBinding
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var register1: ActivityResultLauncher<String>
    private lateinit var register2: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Sign up"

        setUpRegisters()

        mBinding.AutoLocating.setOnClickListener {
            getCurrentAddress()
        }

        mBinding.Next.setOnClickListener {
            if (check()) {
                startActivity(AdminInfoActivity.instance(baseContext))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mGoogleMap = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as GoogleMap
        register1.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        register2.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun setUpRegisters() {
        register1 = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        }

        register2 = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        }
    }

    private fun getCurrentAddress() {
        Address.getAddress(baseContext) {
            TempStorage.instance().restaurantAddress = it
            val icon = getBitmap(R.drawable.restaurant)
            val latLng = LatLng(it.latitude, it.longitude)
            mGoogleMap.addMarker(
                GoogleMap.Marker(
                    latLng,
                    icon,
                    "I am here"
                )
            )
        }
    }

    private fun getBitmap(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(baseContext, drawableId)
        var bitmap: Bitmap? = null

        drawable?.run {
            bitmap = Bitmap.createBitmap(
                intrinsicWidth,
                intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap!!)
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
        }
        return bitmap
    }

    private fun check(): Boolean {
        return (TempStorage.instance().restaurantAddress != null)
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, LocationActivity::class.java)
        }
    }
}