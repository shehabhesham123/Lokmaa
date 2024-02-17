package com.example.admin.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.R
import com.example.admin.databinding.BottomSheetBinding
import com.example.admin.pojo.Order
import com.example.admin.pojo.OrderItem
import com.example.admin.utils.TempStorage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textview.MaterialTextView

class BottomSheet : BottomSheetDialogFragment(), ViewHolder {

    private var mOrder: Order? = null
    private lateinit var mBinding: BottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOrder = TempStorage.instance().order
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = BottomSheetBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.order.text = getString(R.string.Order, mOrder!!.id)
        mBinding.client.text = getString(R.string.client, mOrder!!.client.username)
        mBinding.delivery.text = getString(R.string.delviery, mOrder!!.delivery.username)
        mBinding.date.text = mOrder!!.date
        mBinding.totalPrice.text = getString(R.string.totalPrice, getTotalPrice().toString())

        mBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mBinding.recyclerView.adapter = Adapter2(mOrder!!.items, this)
    }

    private fun getTotalPrice(): Float {
        var total = 0f
        for (i in mOrder!!.items) {
            total += i.type.price
        }
        return total
    }

    override fun setOnCreateViewHolder(parent: ViewGroup): ViewHolder2 {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.order_item, parent, false)
        return OrderItemHolder(view)
    }
}

class OrderItemHolder(viewItem: View) : ViewHolder2(viewItem) {
    private val mTextView = itemView.findViewById<TextView>(R.id.orderItem)
    override fun bind(item: Any) {
        val orderItem = item as OrderItem
        val res = itemView.resources

        mTextView.text = res.getString(
            R.string.orderItem,
            "${orderItem.quantity}",
            " ${orderItem.meal.name}",
            orderItem.type.name
        )
    }
}