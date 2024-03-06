package com.example.lokma.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lokma.R
import com.example.lokma.constant.TempStorage
import com.example.lokma.databinding.FragmentCartBinding
import com.example.lokma.pojo.Cart
import com.example.lokma.pojo.OrderItem
import com.squareup.picasso.Picasso

class CartActivity : Fragment(), ViewHolder, QuantityChanging {
    private lateinit var mBinding: FragmentCartBinding
    private lateinit var listener:CartBacking

    private var mCart = TempStorage.instance().cart!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CartBacking) listener = context
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCartBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.CartFragmentRecyclerViewOrders.hasFixedSize()
        mBinding.CartFragmentRecyclerViewOrders.layoutManager =
            LinearLayoutManager(requireContext())
        mBinding.CartFragmentRecyclerViewOrders.adapter = Adapter2(mCart.items, this)

        mBinding.CartFragmentTotalPrice.text =
            resources.getString(R.string.price, getTotalPrice().toString())

        mBinding.back.setOnClickListener {
            listener.onBackPress()
        }
    }

    private fun getTotalPrice(): Float {
        var res = 0f
        for (i in mCart.items) {
            res += i.quantity * i.type.price
        }
        return res
    }


    override fun setOnCreateViewHolder(parent: ViewGroup): ViewHolder2 {
        val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.one_cart_item, parent, false)
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
        }
    }
}

interface QuantityChanging {
    fun onQuantityChange(quantity: Int, position: Int)
}




