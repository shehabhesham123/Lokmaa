package com.example.admin.ui

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.R

class WelcomeActivity : AppCompatActivity() {
    private lateinit var mThread: SeparatedThread
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        mThread = SeparatedThread()
        mThread.start()
        Handler(mThread.looper).postDelayed({
            val intent = MainActivity.instance(baseContext)
            startActivity(intent)
        }, 2000)

    }

    override fun onDestroy() {
        super.onDestroy()
        mThread.quit()
    }
}

