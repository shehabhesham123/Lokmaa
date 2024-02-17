package com.example.delivery

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log

class Alarm {
    companion object {
        fun startAlarm(context: Context, deliveryUsername: String) {
            val intent = Intent(context, MyService::class.java)
            intent.putExtra("username", deliveryUsername)
            val pendingIntent = PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            Log.i("shehab","start alarm")
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                60000,
                pendingIntent
            )
        }

        fun cancel(context: Context) {
            val pendingIntent = PendingIntent.getService(
                context,
                0,
                Intent(context, MyService::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}