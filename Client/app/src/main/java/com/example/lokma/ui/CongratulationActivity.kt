package com.example.lokma.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lokma.R
import com.example.lokma.databinding.ActivityCongratulationBinding

class CongratulationActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivityCongratulationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCongratulationBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.CongratulationBtnGetStarted.setOnClickListener {
            val intent = LoginActivity.instance(baseContext)
            startActivity(intent)
            finish()
        }
    }

    companion object{
        fun instance(context: Context):Intent{
            val intent = Intent(context,CongratulationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }
    }
}