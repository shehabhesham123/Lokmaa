package com.example.lokma.pojo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lokma.R
import com.example.lokma.databinding.OneAddingBinding
import com.example.lokma.pojo.listener.AddingListener
import com.example.lokma.pojo.model.Food

class AddingAdapter(private val list: List<Food.Adding>, val food: Food,val listener:AddingListener) :
    RecyclerView.Adapter<AddingAdapter.ViewHolder>() {

    class ViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        val binding = OneAddingBinding.bind(viewItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.one_adding, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adding = list[position]
        holder.binding.apply {
            tvSelectPrice.text = adding.price.toString()
            cbSelectOne.text = adding.name
            cbSelectOne.isChecked = adding.isChecked

            cbSelectOne.setOnCheckedChangeListener { _, b ->
                if (b) food.price += adding.price
                else food.price += adding.price
                list[position].isChecked = b
                listener.setOnSelectAddingItem()
            }
        }
    }

    override fun getItemCount() = list.size


}