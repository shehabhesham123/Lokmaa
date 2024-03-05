package com.example.lokma.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.example.lokma.R
import com.example.lokma.constant.TempStorage
import com.example.lokma.databinding.FragmentBottomSheetBinding
import com.example.lokma.pojo.OrderItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso

class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var mBinding: FragmentBottomSheetBinding
    private var meal = TempStorage.instance().meal
    private var orderItem: OrderItem? = null
    private lateinit var listener: OrderItemAdding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OrderItemAdding) listener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentBottomSheetBinding.inflate(inflater)
        return mBinding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        meal?.run {
            orderItem = OrderItem(this, types[0])

            Picasso.get().load(image).into(mBinding.bottomImage)
            mBinding.bottomTvFoodName.text = name
            mBinding.bottomTvTotalPrice.text =
                resources.getString(R.string.price, orderItem!!.type.price.toString())

            for ((idx, type) in types.withIndex()) {
                val radioButton = RadioButton(requireContext())
                //if (idx == 0) radioButton.isChecked = true
                radioButton.textSize = 19f
                radioButton.buttonTintList = resources.getColorStateList(R.color.primaryColor, null)
                radioButton.text =
                    resources.getString(R.string.size_price, type.name, type.price.toString())
                mBinding.sizes.addView(radioButton)
            }
            mBinding.sizes.check(3)

            mBinding.bottomBtnAddToCart.text =
                resources.getString(R.string.add_to_cart_text, orderItem!!.type.price.toString())
        }

        mBinding.bottomBtnAdd.setOnClickListener {
            orderItem!!.quantity++
            quantityChanged()

        }

        mBinding.bottomBtnSubtract.setOnClickListener {
            if (orderItem!!.quantity > 1) {
                orderItem!!.quantity--
                quantityChanged()
            }
        }

        mBinding.sizes.setOnCheckedChangeListener { group, checkedId ->
            val type = meal!!.types[checkedId - 3]
            orderItem!!.type = type
            quantityChanged()
        }

        mBinding.bottomBtnAddToCart.setOnClickListener {
            TempStorage.instance().orderItem = orderItem
            // listener
            listener.onAddOrderItem()
            dismiss()
        }
    }

    private fun quantityChanged() {
        mBinding.bottomTvQuantity.text = orderItem!!.quantity.toString()
        val totalPrice = orderItem!!.quantity * orderItem!!.type.price
        mBinding.bottomTvTotalPrice.text = resources.getString(
            R.string.price,
            totalPrice.toString()
        )
        mBinding.bottomBtnAddToCart.text =
            resources.getString(R.string.add_to_cart_text, totalPrice.toString())
    }

}