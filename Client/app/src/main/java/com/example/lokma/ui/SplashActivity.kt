package com.example.lokma.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.lokma.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var mThread: SeparatedThread
    private lateinit var mSharedPreference: SharedPreferences
    private lateinit var mPreferenceEditor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedPreference = getPreferences(Context.MODE_PRIVATE)
        mPreferenceEditor = mSharedPreference.edit()
        setContentView(R.layout.activity_splash)
        goToNextActivity()
    }

    private fun goToNextActivity() {
        mThread = SeparatedThread()
        mThread.start()
        Handler(mThread.looper).postDelayed({
            val isFirstOpen = mSharedPreference.getBoolean(IS_FIRST_OPEN, true)
            val intent = if (isFirstOpen) {
                mPreferenceEditor.putBoolean(IS_FIRST_OPEN, false)
                mPreferenceEditor.apply()
                IntroActivity.instance(baseContext)
            } else {
                LoginActivity.instance(baseContext)
            }

            startActivity(intent)
            finish()
        }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        mThread.quit()
    }

    companion object {
        const val IS_FIRST_OPEN = "isFirstOpen"
    }
}