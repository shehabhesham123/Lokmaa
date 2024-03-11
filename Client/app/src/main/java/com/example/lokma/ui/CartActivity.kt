package com.example.lokma.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lokma.R
import com.example.lokma.constant.Const
import com.example.lokma.constant.OrderState
import com.example.lokma.constant.TempStorage
import com.example.lokma.databinding.ActivityCartBinding
import com.example.lokma.network.firebase.Firestore
import com.example.lokma.pojo.Address
import com.example.lokma.pojo.Delivery
import com.example.lokma.pojo.Location
import com.example.lokma.pojo.Order
import com.example.lokma.pojo.OrderItem
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date

class CartActivity : AppCompatActivity(), ViewHolder, QuantityChanging, DeliveryListener {
    private lateinit var mBinding: ActivityCartBinding
    private var mCart = TempStorage.instance().cart!!
    private var client = TempStorage.instance().client!!
    private var restaurant = TempStorage.instance().currentRestaurant!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        mBinding.CartFragmentRecyclerViewOrders.hasFixedSize()
        mBinding.CartFragmentRecyclerViewOrders.layoutManager =
            LinearLayoutManager(baseContext)
        mBinding.CartFragmentRecyclerViewOrders.adapter = Adapter2(mCart.items, this)

        mBinding.CartFragmentTotalPrice.text =
            resources.getString(R.string.price, getTotalPrice().toString())


        mBinding.CartFragmentButtonPleaseOrder.setOnClickListener {
            // order
            order()
            mBinding.CartFragmentButtonPleaseOrder.isEnabled = false
        }

        mBinding.CartFragmentIncludeLocation.LocationImageViewChangeLocation.setOnClickListener {
            // go to Location Activity
            Handler(Looper.getMainLooper()).post{
                val intent = LocationActivity.instance(baseContext)
                startActivity(intent)
            }

        }
        s.start()
        Handler(s.looper).post {
            putLocation()
        }

    }

    val s = SeparatedThread()
    override fun onDestroy() {
        super.onDestroy()
        s.quit()
    }

    override fun onRestart() {
        super.onRestart()
        Handler(s.looper).post {
            putLocation()
        }
    }

    private fun order() {
        getBestDelivery()
    }

    private fun getBestDelivery() {
        DeliveryPresenter.getBestOne(baseContext, this)
    }

    @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
    override fun onDeliveryReady(delivery: Delivery) {
        val id = Firestore.documentId()
        val sdf = SimpleDateFormat("dd/mm/yyyy hh:mm:ss")
        val date = sdf.format(Date())
        val order = Order(id, client, delivery, restaurant, date, mCart.items)
        val firestore = Firestore(baseContext)
        firestore.upload(order,Const.MY_ODERDER_PATH(delivery.username),id,{
            firestore.upload(order,Const.ordersPath(restaurant.id!!),id,{
                Toast.makeText(baseContext, "Done", Toast.LENGTH_SHORT).show()
                TempStorage.instance().cart!!.items.clear()
                mBinding.CartFragmentRecyclerViewOrders.adapter?.notifyDataSetChanged()
                mBinding.CartFragmentTotalPrice.text = resources.getString(R.string.price,"0.0")
                mBinding.CartFragmentButtonPleaseOrder.isEnabled =true
            },{})                                                            
        },{})
    }

    private fun putLocation() {
        val resAddress = restaurant.address

        client.address?.let {
            val dis = Location.distance(
                it.latitude,
                it.longitude,
                resAddress.latitude,
                resAddress.longitude
            )
            putLocationData(
                it.address,
                resources.getString(R.string.distance, String.format("%.2f", dis))
            )
            return
        }

        val defaultLocation = Location()
        Address.getAddress(baseContext, defaultLocation) {
            client.address = it
            val dis = Location.distance(
                it.latitude,
                it.longitude,
                resAddress.latitude,
                resAddress.longitude
            )
            putLocationData(
                it.address,
                resources.getString(R.string.distance, String.format("%.2f", dis))
            )
        }
    }

    private fun putLocationData(address: String, distance: String) {
        Handler(Looper.getMainLooper()).post {
            mBinding.CartFragmentIncludeLocation.LocationTextViewLocationAddress.text = address
            mBinding.CartFragmentIncludeLocation.LocationTextViewDistance.text = distance
        }
    }

    private fun getTotalPrice(): Float {
        var res = 0f
        for (i in mCart.items) {
            res += i.quantity * i.type.price
        }
        return res
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }
    }

    override fun setOnCreateViewHolder(parent: ViewGroup): ViewHolder2 {
        val view =
            LayoutInflater.from(baseContext).inflate(R.layout.one_cart_item, parent, false)
        return OrderItemHolder(view, this)
    }

    private class OrderItemHolder(itemView: View, private val listener: QuantityChanging) :
        ViewHolder2(itemView) {
        private val image = itemView.findViewById<ImageView>(R.id.OneCartItem_ImageView_FoodImage)
        private val name = itemView.findViewById<TextView>(R.id.OneCartItem_TextView_FoodName)
        private val quantity = itemView.findViewById<TextView>(R.id.OneCartItem_TextView_Quantity)
        private val price = itemView.findViewById<TextView>(R.id.OneCartItem_TextView_Price)
        private val reduceButton =
            itemView.findViewById<CardView>(R.id.OneCartItem_CardView_ReduceQuantity)
        private val addButton =
            itemView.findViewById<CardView>(R.id.OneCartItem_CardView_AddQuantity)

        override fun bind(item: Any, position: Int) {
            val orderItem = item as OrderItem
            Picasso.get().load(orderItem.meal.image).into(image)
            name.text = orderItem.meal.name
            quantity.text = orderItem.quantity.toString()
            val totalPrice = orderItem.quantity * orderItem.type.price
            price.text = itemView.resources.getString(R.string.price, totalPrice.toString())

            reduceButton.setOnClickListener {
                orderItem.quantity--
                quantityChanged(orderItem, position)
            }
            addButton.setOnClickListener {
                orderItem.quantity++
                quantityChanged(orderItem, position)
            }
        }

        private fun quantityChanged(orderItem: OrderItem, position: Int) {
            quantity.text = orderItem.quantity.toString()
            listener.onQuantityChange(orderItem.quantity, position)
        }

    }

    override fun onQuantityChange(quantity: Int, position: Int) {
        mCart.items[position].quantity = quantity
        mBinding.CartFragmentTotalPrice.text =
            resources.getString(R.string.price, getTotalPrice().toString())
        if (quantity == 0) {
            mCart.items.removeAt(position)
            mBinding.CartFragmentRecyclerViewOrders.adapter?.notifyItemRemoved(position)

            Log.i("shehab hesham"," cart  ${mCart.items.size}  ,  ${TempStorage.instance().cart!!.items.size}")
        }
    }


}

interface QuantityChanging {
    fun onQuantityChange(quantity: Int, position: Int)
}