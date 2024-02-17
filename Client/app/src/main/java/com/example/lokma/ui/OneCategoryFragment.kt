package com.example.lokma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lokma.R
import com.example.lokma.databinding.FragmentOneCategoryBinding
import com.example.lokma.pojo.adapter.FoodAdapter
import com.example.lokma.pojo.listener.FoodListener
import com.example.lokma.pojo.model.Category
import com.example.lokma.pojo.model.Food

private const val CATEGORY_KEY = "Category"

class OneCategoryFragment private constructor() : Fragment() {

    private lateinit var binding: FragmentOneCategoryBinding
    private lateinit var category: Category
    private lateinit var adapter: FoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            category = getSerializable(CATEGORY_KEY) as Category
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_one_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOneCategoryBinding.bind(view)
        adapter = FoodAdapter(category.foods,object :FoodListener{
            override fun setOnClickOnFood(food: Food) {

            }
        })
        binding.menuRecycler.adapter = adapter
        binding.menuRecycler.hasFixedSize()
    }

    companion object {
        fun instance(category: Category): OneCategoryFragment {
            val bundle = Bundle()
            bundle.putSerializable(CATEGORY_KEY,category)
            val fragment = OneCategoryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}