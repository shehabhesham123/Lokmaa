package com.example.lokma.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lokma.R
import com.example.lokma.databinding.FragmentMenuBinding
import com.example.lokma.firebase.Firestore
import com.example.lokma.pojo.adapter.PagerAdapter
import com.example.lokma.pojo.constant.Constant
import com.example.lokma.pojo.listener.MenuListener
import com.example.lokma.pojo.model.Category
import com.example.lokma.pojo.model.Food
import com.google.android.material.tabs.TabLayoutMediator


private const val RESTAURANT_ID_KEY = "RestaurantId"

class MenuFragment private constructor() : Fragment() {

    private lateinit var binding: FragmentMenuBinding
    private lateinit var firestore: Firestore
    private lateinit var listener: MenuListener
    private var restaurantId: String? = null

    private var categories = mutableMapOf<String, Category>()

    private val fragments = mutableListOf<Fragment>()
    private val tabTittles = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            restaurantId = it.getString(RESTAURANT_ID_KEY)
        }

        // initialize Firestore
        firestore = Firestore(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMenuBinding.bind(view)

        getCategory()


        //addDataInBottomSheet()
    }

    private fun setupTabsAndPager() {
        val listForAllCategoryFragment = mutableListOf<Category>()
        val categoryFragments = mutableListOf<Fragment>()
        val categoryNameFragments = mutableListOf<String>()
        for (i in categories) {
            listForAllCategoryFragment.add(i.value)
            categoryFragments.add(OneCategoryFragment.instance(i.value))
            categoryNameFragments.add(i.value.name)
        }
        fragments.add(AllCategoryFragment.instance(listForAllCategoryFragment))
        tabTittles.add("All")
        fragments.addAll(categoryFragments)
        tabTittles.addAll(categoryNameFragments)
    }

    private fun getFood() {
        restaurantId?.let {
            firestore.download(Constant.foodPath(it), { data ->
                for (i in data) {
                    val id = i.id
                    val foodAsMap = i.data
                    foodAsMap?.let {
                        val foodAndCategoryId = Food.fromMapToObj(id, foodAsMap)
                        val categoryId = foodAndCategoryId.second
                        val food = foodAndCategoryId.first
                        categories[categoryId]?.foods?.add(food)
                    }
                }

                for (i in categories){
                    Log.i("asdf","${i.value.name}")
                }
                setupTabsAndPager()
                initViewPager()
                initTabLayout()
            }, {

            })
        }
    }

    private fun getCategory() {
        restaurantId?.let {
            firestore.download(Constant.categoryPath(it), { data ->
                for (i in data) {
                    val id = i.id
                    val map = i.data
                    map?.let {
                        categories[id] = Category.fromMapToObj(id, map)
                    }
                }
                getFood()
            }, {

            })
        }
    }

    private fun initTabLayout() {
        TabLayoutMediator(binding.tlMenuTabs, binding.vpMenu) { tab, position ->
            tab.text = tabTittles[position]
        }.attach()

    }

    private fun initViewPager() {
        val pagerAdapter = PagerAdapter(requireActivity(), fragments)
        binding.vpMenu.adapter = pagerAdapter
    }

    /*
        private fun addDataInBottomSheet() {
            val add1 = AddSomeFood("sos", 15.5, false)
            for (i in 0..1) {
                DataManager.addTopping(add1)
            }
        }
        */

    companion object {
        fun instance(restaurantId: String): MenuFragment {
            val bundle = Bundle()
            bundle.putString(RESTAURANT_ID_KEY, restaurantId)
            val fragment = MenuFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}