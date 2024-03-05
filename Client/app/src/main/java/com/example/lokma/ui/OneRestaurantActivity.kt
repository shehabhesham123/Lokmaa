package com.example.lokma.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.lokma.constant.TempStorage
import com.example.lokma.databinding.ActivityOneRestaurantBinding
import com.example.lokma.pojo.Menu
import com.example.lokma.pojo.Restaurant
import com.google.android.material.tabs.TabLayoutMediator

class OneRestaurantActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityOneRestaurantBinding
    private lateinit var mRestaurant: Restaurant
    private lateinit var mMenu: Menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityOneRestaurantBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mRestaurant = TempStorage.instance().currentRestaurant!!
        mMenu = mRestaurant.menu!!

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.vpMenu.adapter = ViewPagerAdapter(getFragments(), this)
        TabLayoutMediator(
            mBinding.tlMenuTabs, mBinding.vpMenu
        ) { tab, position ->
            val category = mMenu.categories[position]
            tab.text = category.name
        }.attach()
    }

    private fun getFragments(): MutableList<OneCategoryFragment> {
        val list = mutableListOf<OneCategoryFragment>()
        for (i in mMenu.categories) {
            list.add(OneCategoryFragment.instance(i.meals))
        }
        return list
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, OneRestaurantActivity::class.java)
        }
    }
}

class ViewPagerAdapter(
    private val fragments: MutableList<OneCategoryFragment>,
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}