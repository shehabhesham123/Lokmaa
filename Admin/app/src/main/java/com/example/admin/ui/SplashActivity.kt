package com.example.admin.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var mThread: SeparatedThread
    private lateinit var mBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        goToMainActivity()
    }

    private fun goToMainActivity() {
        mThread = SeparatedThread()
        mThread.start()
        Handler(mThread.looper).postDelayed({
            val intent = RegistrationActivity.instance(baseContext)
            startActivity(intent)
            finish()
        }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        mThread.quit()
    }
}

