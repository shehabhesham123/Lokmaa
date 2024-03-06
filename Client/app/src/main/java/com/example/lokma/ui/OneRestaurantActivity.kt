package com.example.lokma.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.lokma.R
import com.example.lokma.constant.TempStorage
import com.example.lokma.databinding.ActivityOneRestaurantBinding
import com.example.lokma.pojo.Cart
import com.example.lokma.pojo.Menu
import com.example.lokma.pojo.Restaurant
import com.google.android.material.tabs.TabLayoutMediator

class OneRestaurantActivity : AppCompatActivity(), OrderItemAdding,CartBacking {
    private lateinit var mBinding: ActivityOneRestaurantBinding
    private lateinit var mRestaurant: Restaurant
    private lateinit var mMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityOneRestaurantBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mRestaurant = TempStorage.instance().currentRestaurant!!
        mMenu = mRestaurant.menu!!
        TempStorage.instance().cart = Cart(mRestaurant.id!!, mutableListOf())

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

        mBinding.CustomCart.root.setOnClickListener {
            // go to cartActivity
            mBinding.oneRestaurant.visibility = View.GONE
            mBinding.Cart.visibility = View.VISIBLE

            val tr = supportFragmentManager.beginTransaction()
            tr.add(R.id.Cart, CartActivity())
            tr.commit()
            cartIsOpen = true
        }

    }

    var cartIsOpen = false
    override fun onBackPressed() {
        if (cartIsOpen) {
            mBinding.oneRestaurant.visibility = View.VISIBLE
            mBinding.Cart.visibility = View.GONE
            cartUIUpdate()
        } else super.onBackPressed()
        cartIsOpen = false
    }


    private fun getFragments(): MutableList<OneCategoryFragment> {
        val list = mutableListOf<OneCategoryFragment>()
        for (i in mMenu.categories) {
            list.add(OneCategoryFragment.instance(i.name))
        }
        return list
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, OneRestaurantActivity::class.java)
        }
    }

    override fun onAddOrderItem() {
        val orderItem = TempStorage.instance().orderItem
        Toast.makeText(baseContext, "${orderItem?.meal?.name} is added to cart", Toast.LENGTH_SHORT)
            .show()
        TempStorage.instance().cart!!.items.add(orderItem!!)
        cartUIUpdate()
    }

    private fun cartUIUpdate() {
        val cart = TempStorage.instance().cart!!
        if (cart.items.isNotEmpty()) {
            mBinding.CustomCart.cartNumCardView.visibility = View.VISIBLE
            mBinding.CustomCart.cartNum.text = cart.items.size.toString()
        }else{
            mBinding.CustomCart.cartNumCardView.visibility = View.GONE
        }
    }

    override fun onBackPress() {
        onBackPressed()
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

interface OrderItemAdding {
    fun onAddOrderItem()
}

interface CartBacking{
    fun onBackPress()
}