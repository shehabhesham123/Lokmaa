package com.example.lokma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.lokma.R
import com.example.lokma.databinding.FragmentOneRestaurantBinding
import com.example.lokma.pojo.adapter.PagerAdapter
import com.example.lokma.pojo.model.Restaurant
import com.google.android.material.tabs.TabLayoutMediator

private const val RESTAURANT_KEY = "Restaurant"
private const val USER_EMAIL_KEY = "UserEmail"

class OneRestaurantFragment : Fragment() {

    private lateinit var binding: FragmentOneRestaurantBinding
    private val fragments = mutableListOf<Fragment>()

    private val tabIcons = listOf(R.drawable.ic_home, R.drawable.ic_menu, R.drawable.ic_cart)

    private var userEmail: String? = null
    private var restaurant: Restaurant? = null

    private lateinit var homeFragment: HomeFragment
    private lateinit var menuFragment: MenuFragment
    private lateinit var cartFragment: CartFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            userEmail = getString(USER_EMAIL_KEY)
            restaurant = getSerializable(RESTAURANT_KEY) as Restaurant
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one_restaurant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOneRestaurantBinding.bind(view)
        homeFragment = HomeFragment.instance(restaurant!!, userEmail)
        cartFragment = CartFragment.instance(restaurant!!, userEmail!!)
        menuFragment = MenuFragment.instance(restaurant!!.id)
        if (fragments.size !=3)
            fragments.addAll(listOf(homeFragment, menuFragment, cartFragment))
        initViewPager()
        initTabLayout()
    }

    companion object {
        fun instance(restaurant: Restaurant, userEmail: String): OneRestaurantFragment {
            val bundle = Bundle()
            bundle.putSerializable(RESTAURANT_KEY, restaurant)
            bundle.putString(USER_EMAIL_KEY, userEmail)
            val fragment = OneRestaurantFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    fun refresh(){
        homeFragment.refresh()
        cartFragment.refresh()
    }

    private fun initTabLayout() {
        TabLayoutMediator(
            binding.OneRestaurantFragmentTabLayout,
            binding.OneRestaurantFragmentViewPager
        ) { tab, position ->
            //tab.text = tabTittles[position]
            tab.icon = ResourcesCompat.getDrawable(resources, tabIcons[position], null)
        }.attach()

    }

    private fun initViewPager() {
        val pagerAdapter = PagerAdapter(requireActivity(), fragments)
        binding.OneRestaurantFragmentViewPager.adapter = pagerAdapter
    }
}