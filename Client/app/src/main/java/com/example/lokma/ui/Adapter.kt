package com.example.lokma.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder


class Adapter2(
    var items: MutableList<*>,
    private val createViewHolder: com.example.lokma.ui.ViewHolder
) :
    RecyclerView.Adapter<ViewHolder2>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder2 {
        return createViewHolder.setOnCreateViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder2, position: Int) {
        items[position]?.let { holder.bind(it) }
    }
}

abstract class ViewHolder2(viewItem: View) : ViewHolder(viewItem) {
    abstract fun bind(item: Any)
}

interface ViewHolder {
    fun setOnCreateViewHolder(parent: ViewGroup): ViewHolder2
}