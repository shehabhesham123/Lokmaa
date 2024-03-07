package com.example.lokma.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import java.util.Locale

class OneRestaurantActivity : AppCompatActivity(), OrderItemAdding {
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
            val intent = CartActivity.instance(baseContext)
            startActivity(intent)
        }

        search()

    }

    override fun onRestart() {
        super.onRestart()
        cartUIUpdate()
    }

    private fun search(){
        mBinding.Search.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val categories = mMenu.categories
                for ((idx,category) in categories.withIndex()){
                    val name = category.name.lowercase(Locale.ROOT)
                    val s2 = s.toString().lowercase(Locale.ROOT)
                    if (name.contains(s2)){
                        mBinding.vpMenu.currentItem = idx
                        return
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
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
        } else {
            mBinding.CustomCart.cartNumCardView.visibility = View.GONE
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

interface OrderItemAdding {
    fun onAddOrderItem()
}
