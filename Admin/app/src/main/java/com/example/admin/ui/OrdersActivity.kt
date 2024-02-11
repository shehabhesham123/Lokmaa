package com.example.admin.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.backend.firebase.Firestore
import com.example.admin.databinding.ActivityOrdersBinding
import com.example.admin.pojo.Address
import com.example.admin.pojo.Client
import com.example.admin.pojo.Delivery
import com.example.admin.pojo.Meal
import com.example.admin.pojo.Order
import com.example.admin.pojo.OrderItem
import com.example.admin.pojo.Restaurant
import com.example.admin.utils.Const
import com.example.admin.utils.TempStorage
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration

class OrdersActivity : AppCompatActivity(), OrderListener {
    private lateinit var mBinding: ActivityOrdersBinding
    private lateinit var firestore: Firestore
    private var mSnapshot: ListenerRegistration? = null
    private var mRestaurant: Restaurant? = null
    private var orders = mutableListOf<Order>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mRestaurant = TempStorage.instance().restaurant
        firestore = Firestore(baseContext)
        mRestaurant?.run {
            mSnapshot = firestore.addSnapshot(Const.ordersPath(id!!), {
                for (i in it) {
                    if (i.whatHappened == DocumentChange.Type.ADDED)
                        i.obj.toObject(Order::class.java)
                }
            }, {

            })
        }
        orders.add(
            Order(
                "1", Client("Ali", Address(1.0, 1.0, ""), ""),
                Delivery("Hassan", Address(1.0, 1.0, "")), "15/2/2024",
                mutableListOf(OrderItem(Meal("name","image"),4))
            )
        )

        itemTouch()
        mBinding.recyclerView.layoutManager = LinearLayoutManager(baseContext)
        mBinding.recyclerView.adapter = OrderAdapter(orders, this)


    }

    fun itemTouch() {
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Toast.makeText(baseContext, "${viewHolder.adapterPosition}", Toast.LENGTH_SHORT)
                    .show()
            }
        }).attachToRecyclerView(mBinding.recyclerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSnapshot?.remove()
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, OrdersActivity::class.java)
        }
    }

    override fun onClick(order: Order) {

        TempStorage.instance().order = order
        val bottomSheet = BottomSheet()
        bottomSheet.show(supportFragmentManager, null)
    }

    private class OrderAdapter(
        var orders: MutableList<Order>,
        private var listener: OrderListener
    ) :
        RecyclerView.Adapter<OrderAdapter.OrderHolder>() {
        class OrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val mTextView = itemView.findViewById<TextView>(R.id.OrderId)

            fun bind(id: String) {
                val r = itemView.context.resources
                val cardView = itemView as CardView
                mTextView.text = r.getString(R.string.Order, id)
                /*if (position % 2 == 0)
                    cardView.setCardBackgroundColor(r.getColor(R.color.primaryColor, null))
                else{
                    cardView.setCardBackgroundColor(r.getColor(R.color.primaryTextColor, null))
                    mTextView.setTextColor(r.getColor(R.color.primaryColor, null))
                }*/
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.one_order, parent, false)
            return OrderHolder(view)
        }

        override fun getItemCount(): Int {
            return orders.size
        }

        override fun onBindViewHolder(holder: OrderHolder, position: Int) {
            holder.bind(orders[position].id)
            holder.itemView.setOnClickListener {
                listener.onClick(orders[position])
                //listener.onClick(orders[position])
            }
        }
    }
}

interface OrderListener {
    fun onClick(order: Order)
}