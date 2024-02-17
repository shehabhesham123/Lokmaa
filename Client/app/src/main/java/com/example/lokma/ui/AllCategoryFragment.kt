package com.example.lokma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lokma.R
import com.example.lokma.databinding.FragmentAllCategoryBinding
import com.example.lokma.pojo.adapter.AllCategoryAdapter
import com.example.lokma.pojo.model.Category
import java.io.Serializable

private const val ALL_CATEGORY_KEY = "AllCategory"

class AllCategoryFragment private constructor(): Fragment() {

    lateinit var binding: FragmentAllCategoryBinding
    private lateinit var allCategoryAdapter: AllCategoryAdapter
    private lateinit var allCategory: List<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            allCategory = convertFromAnyToCategory(getSerializable(ALL_CATEGORY_KEY) as List<*>)
        }
    }

    private fun convertFromAnyToCategory(list: List<*>): List<Category> {
        val allCategory = mutableListOf<Category>()
        for (i in list) allCategory.add(i as Category)
        return allCategory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAllCategoryBinding.bind(view)

        setAllCategoryRecycler(allCategory)
    }

    private fun setAllCategoryRecycler(allCategory: List<Category>) {
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.AllCategoryFragmentRecyclerView.layoutManager = layoutManager
        allCategoryAdapter = AllCategoryAdapter(requireContext(), allCategory)
        binding.AllCategoryFragmentRecyclerView.adapter = allCategoryAdapter
    }

    companion object {
        fun instance(allCategory: List<Category>): AllCategoryFragment {
            val bundle = Bundle()
            bundle.putSerializable(ALL_CATEGORY_KEY, allCategory as Serializable)
            val fragment = AllCategoryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}