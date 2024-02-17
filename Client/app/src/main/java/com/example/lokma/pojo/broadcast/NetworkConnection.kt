package com.example.lokma.pojo.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.lokma.R
import com.example.lokma.pojo.constant.Constant

class NetworkConnection(private val textView: TextView,private val locationLambda:()->Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val isOnline = isOnline(context)
        changeState(context, isOnline)
        if (isOnline){
            online()
            locationLambda()
        }
        else offline()
    }

    private fun offline() {
        textView.text = textView.context.resources.getString(R.string.noInternetConnection)
        textView.background =
            ResourcesCompat.getDrawable(textView.resources, R.color.noInternetConnectionState, null)
        textView.visibility = View.INVISIBLE
        val animation = AnimationUtils.loadAnimation(textView.context, R.anim.offline)
        Handler().postDelayed({
            textView.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    textView.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(p0: Animation?) {

                }

                override fun onAnimationRepeat(p0: Animation?) {

                }
            })
        }, 1000)

    }

    private fun online() {
        textView.text = textView.context.resources.getString(R.string.internetConnection)
        textView.background =
            ResourcesCompat.getDrawable(textView.resources, R.color.internetConnectionState, null)
        val animation = AnimationUtils.loadAnimation(textView.context, R.anim.online)
        Handler().postDelayed({
            textView.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    textView.visibility = View.GONE
                }

                override fun onAnimationRepeat(p0: Animation?) {

                }
            })
        }, 1000)
    }

    @SuppressLint("CommitPrefEdits")
    private fun changeState(context: Context, isOnline: Boolean) {
        val sp = context.getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putBoolean(Constant.networkConnectionKey, isOnline)
        edit.apply()
    }

    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        //should check null because in airplane mode it will be null
        return netInfo != null && netInfo.isConnected
    }
}

