package com.example.delivery

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.delivery.backend.firebase.Firestore
import com.example.delivery.pojo.Order
import com.example.delivery.ui.OrderView
import com.example.delivery.ui.OrdersPresenter
import com.example.delivery.ui.SignInActivity
import com.example.delivery.utils.Const
import com.example.delivery.utils.OrderState

class MyService : Service(), OrderView {

    private var mDeliveryUsername: String? = null

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        mDeliveryUsername = intent?.run {
            this.getStringExtra("username")
        }

        mDeliveryUsername?.let {
            val ordersPresenter = OrdersPresenter(this)
            ordersPresenter.getOrders(baseContext, it)
        }

        return START_STICKY
    }

    private fun createNotification(restaurantAddress: String) {
        val pendingIntent = PendingIntent.getActivity(
            baseContext,
            0,
            Intent(baseContext, SignInActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = NotificationManagerCompat.from(baseContext)

        val channelId = "channel"
        val channelName = "channel"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) NotificationCompat.Builder(
                baseContext,
                channelId
            )
            else NotificationCompat.Builder(baseContext)

        notification.setContentTitle("New order")
            .setSmallIcon(R.drawable.logo)
            .setContentText("Go to $restaurantAddress immediately to receive the order")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 1000, 1000, 1000)).setContentIntent(pendingIntent)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(notificationId, notification.build())
        }
    }

    override fun onGetOrders(orders: MutableList<Order>) {
        if (orders.isNotEmpty()) {
            for (i in orders) {
                if (i.state != OrderState.NOTIFICATION_RECEIVE_TO_DELIVERY) {
                    createNotification(i.restaurant.address.address)
                    updateState(i)
                }
            }
        }
    }

    private fun updateState(order: Order) {
        order.state = OrderState.NOTIFICATION_RECEIVE_TO_DELIVERY
        val firestore = Firestore(baseContext)
        firestore.update(order, "${Const.MY_ODERDER_PATH(mDeliveryUsername!!)}/${order.id}", {}, {})
    }
}