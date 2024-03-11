package com.example.delivery.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.delivery.R
import com.example.delivery.databinding.DialogLayoutBinding
import com.example.delivery.pojo.OrderItem
import com.example.delivery.utils.TempStorage

class Dialog(private val listener: DeliveryListener) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val order = TempStorage.instance().order
        val mBinding = DialogLayoutBinding.inflate(layoutInflater)
        order?.run {
            mBinding.clientAddress.text =
                getString(R.string.clientAddress, client.address.address)
            mBinding.RestaurantAddress.text =
                getString(R.string.restaurantAddress, restaurant.address.address)
            mBinding.price.text =
                getString(R.string.clientAddress, getPrice(order.items).toString())
        }
        mBinding.iDelivered.setOnClickListener {
            listener.onArrive()
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(mBinding.root)
            .create()
    }

    private fun getPrice(orderItems: MutableList<OrderItem>): Float {
        var totalPrice = 0f
        for (i in orderItems) {
            totalPrice += i.type.price
        }
        return totalPrice
    }
}