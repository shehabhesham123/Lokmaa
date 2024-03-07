package com.example.delivery.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.delivery.Alarm
import com.example.delivery.R
import com.example.delivery.backend.firebase.Firestore
import com.example.delivery.backend.firebase.NormalAuth
import com.example.delivery.databinding.ActivityMainBinding
import com.example.delivery.pojo.Address
import com.example.delivery.pojo.Admin
import com.example.delivery.pojo.Client
import com.example.delivery.pojo.Delivery
import com.example.delivery.pojo.Menu
import com.example.delivery.pojo.Order
import com.example.delivery.pojo.Restaurant
import com.example.delivery.utils.Const
import com.example.delivery.utils.OrderState
import com.example.delivery.utils.Rating
import com.example.delivery.utils.TempStorage
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.tapadoo.alerter.Alerter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@Suppress("KotlinConstantConditions", "SameParameterValue")
class MainActivity : AppCompatActivity(), DeliveryListener {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mGoogleMap: GoogleMap
    private var mDelivery: Delivery? = null
    private lateinit var mFirestore: Firestore
    private var listenerRegistration: ListenerRegistration? = null
    private lateinit var permissionRegistration: ActivityResultLauncher<String>
    private var thereIsOrder = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.title = ""

        requirePermissions()

        mFirestore = Firestore(baseContext)

        val delivery = TempStorage.instance().delivery
        if (delivery != null) mDelivery = delivery
        else {
            startActivity(SignInActivity.instanceWithClearStack(baseContext))
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
        Alarm.cancel(baseContext)
    }

    override fun onResume() {
        super.onResume()
        permissionRegistration.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        permissionRegistration.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionRegistration.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        check()
    }

    override fun onStop() {
        super.onStop()
        Alarm.startAlarm(baseContext, mDelivery!!.username)
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }

    private fun requirePermissions() {
        permissionRegistration =
            registerForActivityResult(ActivityResultContracts.RequestPermission(), {})
    }

    private fun check() {
        listenerRegistration = mFirestore.addSnapshot(Const.MY_ODERDER_PATH(mDelivery!!.username), {
            if (it.isEmpty()) {
                isWaiting()
            } else {
                for (i in it) {
                    if (i.whatHappened == DocumentChange.Type.ADDED) {
                        val order = i.obj.toObject(Order::class.java)
                        if (order.state != OrderState.ARRIVED) {
                            TempStorage.instance().order = order
                            thereIsOrder = true
                            invalidateOptionsMenu()
                            newOrderAdded(order)
                            if (order.state == OrderState.NOTIFICATION_NOT_RECEIVE_TO_DELIVERY) {
                                createAlerter(order.restaurant.address.address)
                                updateState(order, OrderState.NOTIFICATION_RECEIVE_TO_DELIVERY)
                            }
                            break
                        }
                    }
                }
                if (!thereIsOrder) {
                    isWaiting()
                }
            }
        }, {

        })
    }

    private fun createAlerter(restaurantAddress: String) {
        Alerter.create(this).setIcon(R.drawable.attention).setTitle("New order added")
            .setText("Go to $restaurantAddress immediately to receive the order").setDuration(3000)
            .setBackgroundColorRes(R.color.primaryColor)
            .enableSwipeToDismiss().show()
    }

    private fun isWaiting() {
        waitingStateUI()
    }

    private fun newOrderAdded(order: Order) {
        addGoogleMapToContainer()
        deliveryStateUI()
        val client = order.client
        val restaurant = order.restaurant
        markAddresses(client.address, restaurant.address)
    }

    private fun waitingStateUI() {
        mBinding.waitingState.visibility = View.VISIBLE
        mBinding.deliveryState.visibility = View.GONE
        mBinding.state.text = spannableText(getString(R.string.waiting), 0, 13)
    }

    private fun deliveryStateUI() {
        mBinding.waitingState.visibility = View.GONE
        mBinding.deliveryState.visibility = View.VISIBLE
    }

    private fun addGoogleMapToContainer() {
        mGoogleMap = GoogleMap()
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, mGoogleMap).commit()
    }

    private fun spannableText(text: String, start: Int, end: Int): SpannableStringBuilder {
        val spannableString = SpannableStringBuilder(text)
        val textColor = ForegroundColorSpan(resources.getColor(R.color.primaryColor, null))
        spannableString.setSpan(textColor, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val bold = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(bold, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun markAddresses(clientAddress: Address, restaurantAddress: Address) {
        val clientLatLng = LatLng(clientAddress.latitude, clientAddress.longitude)
        val restaurantLatLng = LatLng(restaurantAddress.latitude, restaurantAddress.longitude)
        val clientMarker = GoogleMap.Marker(
            clientLatLng, getBitmap(R.drawable.client_location), "Client Location"
        )

        val restaurantMarker = GoogleMap.Marker(
            restaurantLatLng, getBitmap(R.drawable.restaurant_location), "Restaurant Location"
        )

        GlobalScope.launch(Dispatchers.Main) {
            val task1 = async { mGoogleMap.addMarker(clientMarker) }
            val task2 = async { mGoogleMap.addMarker(restaurantMarker) }

            if (task1.await() == task2.await()) {
                mGoogleMap.moveCamera(clientLatLng, restaurantLatLng)
            }
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

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.m1, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: android.view.Menu?): Boolean {
        val orderInfo = menu?.findItem(R.id.orderInfo)
        orderInfo?.isVisible = thereIsOrder
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.orderInfo -> {
                val dialog = Dialog(this)
                dialog.show(supportFragmentManager, null)
            }

            R.id.signOut -> {
                val auth = NormalAuth(baseContext)
                auth.signOut()
                startActivity(SignInActivity.instanceWithClearStack(baseContext))
                finish()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private fun test() {
        val firestore = Firestore(baseContext)
        val id = Firestore.documentId()
        val order = Order(
            id,
            Client("Ali Mohamed", "", Address(27.188542, 31.191890, "Address"), "01067770465"),
            Delivery("Hassan Ahmed", "", "01067770524"),
            Restaurant(
                "12034410025",
                "Bazooka",
                "",
                Address(27.182395, 31.177256, "Address"),
                Rating.NORMAL,
                Menu(),
                Admin("Hassan Ahmed", "", "01067770544")
            ),
            "12/2/2024",
            mutableListOf()
        )

        firestore.upload(order, Const.MY_ODERDER_PATH(mDelivery!!.username), id, {}, {})
    }

    override fun onArrive() {
        val order = TempStorage.instance().order
        order?.let {
            updateState(it, OrderState.ARRIVED)
            thereIsOrder = false
            invalidateOptionsMenu()
            isWaiting()
            Toast.makeText(baseContext, "Thanks", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateState(order: Order, state: Int) {
        order.state = state
        val firestore = Firestore(baseContext)
        firestore.update(
            order,
            "${Const.MY_ODERDER_PATH(mDelivery!!.username)}/${order.id}",
            {},
            {})
    }
}

interface DeliveryListener {
    fun onArrive()
}