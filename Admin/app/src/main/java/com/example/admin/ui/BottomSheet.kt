package com.example.admin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.admin.R
import com.example.admin.databinding.BottomSheetBinding
import com.example.admin.pojo.Order
import com.example.admin.pojo.OrderItem
import com.example.admin.utils.TempStorage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textview.MaterialTextView

class BottomSheet : BottomSheetDialogFragment() {

    private var order: Order? = null
    private lateinit var mBinding: BottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        order = TempStorage.instance().order
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
        mBinding.order.text = getString(R.string.Order, order!!.id)
        mBinding.client.text = getString(R.string.client, order!!.client.name)
        mBinding.delivery.text = getString(R.string.delviery, order!!.delivery.name)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mBinding.recyclerView.adapter = OrderItemsAdapter(order!!.items)
    }


    class OrderItemsAdapter(val orderItems:MutableList<OrderItem>):RecyclerView.Adapter<OrderItemsAdapter.VH>(){

        class VH(viewItem:View):ViewHolder(viewItem){
            private val mTextView = viewItem.findViewById<MaterialTextView>(R.id.orderItem)
            fun bind(orderItem: OrderItem){
                val res = itemView.resources
                mTextView.text = res.getString(R.string.orderItem,"${orderItem.quantity}"," ${orderItem.meal.name}")
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item,parent,false)
            return VH(view)
        }

        override fun getItemCount(): Int {
            return orderItems.size
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(orderItems[position])
        }

    }

}