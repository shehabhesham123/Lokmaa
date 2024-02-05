package com.example.admin.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.R
import com.example.admin.databinding.ActivityLocationBinding

class LocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            Toast.makeText(baseContext, "ACCESS_NETWORK_STATE $it", Toast.LENGTH_SHORT).show()
        }.launch(android.Manifest.permission.ACCESS_NETWORK_STATE)

        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            Toast.makeText(baseContext, "ACCESS_FINE_LOCATION $it", Toast.LENGTH_SHORT).show()
        }.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)

        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            Toast.makeText(baseContext, "ACCESS_COARSE_LOCATION $it", Toast.LENGTH_SHORT).show()

        }.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)


        /*
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
*/


    }

    companion object{
        fun instance(context:Context): Intent{
            return Intent(context,LocationActivity::class.java)
        }
    }
}