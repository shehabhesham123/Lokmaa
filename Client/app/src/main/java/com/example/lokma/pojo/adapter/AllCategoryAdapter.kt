package com.example.lokma.pojo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lokma.R
import com.example.lokma.databinding.OneCategoryInAllCategoryFragmentBinding
import com.example.lokma.pojo.listener.FoodListener
import com.example.lokma.pojo.model.Category
import com.example.lokma.pojo.model.Food

class AllCategoryAdapter(
    private val context: Context,
    private val categories: List<Category>
) : RecyclerView.Adapter<AllCategoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //var  categoryTitle:TextView = itemView.findViewById(R.id.tv_title)
        val binding = OneCategoryInAllCategoryFragmentBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.one_category_in_all_category_fragment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.apply {
            tvTitle.text = category.name
            putItemsDataOfCategory(recyclerItems, category.foods)
        }
    }

    override fun getItemCount() = categories.size

    private fun putItemsDataOfCategory(recyclerView: RecyclerView, categoryItems: MutableList<Food>) {
        val itemRecyclerAdapter = FoodAdapter(categoryItems,object :FoodListener{
            override fun setOnClickOnFood(food: Food) {

            }
        })
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = itemRecyclerAdapter
    }
}