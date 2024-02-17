package com.example.admin.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.admin.R
import com.example.admin.backend.firebase.Firestore
import com.example.admin.backend.firebase.NormalAuth
import com.example.admin.databinding.ActivityMainBinding
import com.example.admin.pojo.Address
import com.example.admin.pojo.Client
import com.example.admin.pojo.Delivery
import com.example.admin.pojo.Meal
import com.example.admin.pojo.Order
import com.example.admin.pojo.OrderItem
import com.example.admin.pojo.Restaurant
import com.example.admin.utils.Const
import com.example.admin.utils.TempStorage

class MainActivity : AppCompatActivity(), RestaurantView {
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.title = ""

        getRestaurantInfo()

    }

    private fun updateUI() {
        val frag = supportFragmentManager.findFragmentById(R.id.FragmentContainer)
        if (frag == null) {
            putFragment(OrdersFragment())
        }
    }

    private fun putFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, fragment)
            .commit()
    }

    private fun getRestaurantInfo() {
        val resPresenter = RestaurantPresenter(this)
        resPresenter.getRestaurant(baseContext)
    }

    override fun onGetRestaurant(restaurant: Restaurant) {
        TempStorage.instance().restaurant = restaurant

        //test(restaurant)

        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(baseContext).inflate(R.menu.menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val order = menu?.findItem(R.id.order)
        val menu2 = menu?.findItem(R.id.menu)
        order?.setOnMenuItemClickListener {
            menu2?.isVisible = true
            order.isVisible = false
            false
        }
        menu2?.setOnMenuItemClickListener {
            menu2.isVisible = false
            order?.isVisible = true
            false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.order -> {
                Toast.makeText(baseContext, "Order", Toast.LENGTH_SHORT).show()
                putFragment(OrdersFragment())
            }

            R.id.menu -> {
                Toast.makeText(baseContext, "Menu", Toast.LENGTH_SHORT).show()
                putFragment(MenuFragment())
            }

            R.id.signOut -> {
                val auth = NormalAuth(baseContext)
                auth.signOut()
                startActivity(SignInActivity.instanceWithClearStack(baseContext))
                finish()
            }

            else -> return false
        }
        return true
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
/*
    private fun test(restaurant: Restaurant) {
        val firestore = Firestore(baseContext)
        val id = Firestore.documentId()
        val order = Order(
            id,
            Client("Ali Mohamed", Address(1.2, 1.2, "Address"), "01067770465"),
            Delivery("Hassan Ahmed", Address(1.2, 1.2, "Address")),
            "12/02/2024",
            mutableListOf(
                OrderItem(Meal("Chicken", ""), 3, Meal.Type("Small", 50f)),
                OrderItem(Meal("Beef", ""), 1, Meal.Type("Medium", 70f)),
                OrderItem(Meal("Pizza", ""), 2, Meal.Type("Large", 100f)),
                OrderItem(Meal("Chicken", ""), 3, Meal.Type("Small", 50f)),
                OrderItem(Meal("Beef", ""), 1, Meal.Type("Small", 50f)),
                OrderItem(Meal("Pizza", ""), 2, Meal.Type("Small", 50f))
            )
        )
        firestore.upload(order, Const.ordersPath(restaurant.id!!), id, {}, {})
    }

 */
}
