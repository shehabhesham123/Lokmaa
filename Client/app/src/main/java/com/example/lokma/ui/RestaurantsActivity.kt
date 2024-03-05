package com.example.lokma.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lokma.R
import com.example.lokma.constant.TempStorage
import com.example.lokma.databinding.ActivityRestaurantsBinding
import com.example.lokma.pojo.Restaurant
import com.squareup.picasso.Picasso

class RestaurantsActivity : AppCompatActivity(), RestaurantListener, ViewHolder, ResListener {
    private lateinit var mBinding: ActivityRestaurantsBinding
    private lateinit var restaurants: MutableList<Restaurant>
    private var shape = true  // shape1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRestaurantsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        getRestaurantsData()
        restaurantsShapesRadioButton()
        search()
    }

    private fun search() {
        mBinding.Search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val res = s.toString()
                val result = mutableListOf<Restaurant>()
                for (i in restaurants) {
                    val isFound = i.name.contains(res)
                    if (isFound) {
                        result.add(i)
                    }
                }
                val list = if (res.trim().isEmpty()) restaurants else result
                updateSearchUI(list)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateSearchUI(result: MutableList<Restaurant>) {
        updateUI(result)
    }

    private fun getRestaurantsData() {
        RestaurantsPresenter.getRestaurants(baseContext, this)
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, RestaurantsActivity::class.java)
        }
    }

    override fun setOnRestaurantsIsReady(restaurants: MutableList<Restaurant>) {
        TempStorage.instance().restaurants = restaurants
        this.restaurants = TempStorage.instance().restaurants
        Toast.makeText(baseContext, "${restaurants.size}", Toast.LENGTH_SHORT).show()
        updateUI(restaurants)
    }

    private fun updateUI(restaurants: MutableList<Restaurant>) {
        if (restaurants.isNotEmpty()) {
            mBinding.AllRestaurantsFragmentLoading.visibility = View.INVISIBLE
            mBinding.AllRestaurantsFragmentRecyclerViewRestaurants.visibility = View.VISIBLE
            mBinding.AllRestaurantsFragmentAnimationViewNoData.visibility = View.INVISIBLE
            setupRecyclerView(restaurants)
        } else {
            mBinding.AllRestaurantsFragmentLoading.visibility = View.INVISIBLE
            mBinding.AllRestaurantsFragmentRecyclerViewRestaurants.visibility = View.INVISIBLE
            mBinding.AllRestaurantsFragmentAnimationViewNoData.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView(restaurants: MutableList<Restaurant>) {
        mBinding.AllRestaurantsFragmentRecyclerViewRestaurants.layoutManager =
            LinearLayoutManager(baseContext)
        mBinding.AllRestaurantsFragmentRecyclerViewRestaurants.adapter = Adapter2(restaurants, this)
    }

    private fun restaurantsShapesRadioButton() {
        mBinding.AllRestaurantsFragmentRadioButtonShape1.setOnClickListener {
            if (!shape) {
                mBinding.AllRestaurantsFragmentRadioButtonShape2.isChecked = false
                updateUIShape1()
                shape = !shape
            }
        }

        mBinding.AllRestaurantsFragmentRadioButtonShape2.setOnClickListener {
            if (shape) {
                mBinding.AllRestaurantsFragmentRadioButtonShape1.isChecked = false
                updateUIShape2()
                shape = !shape
            }

        }
    }

    private fun updateUIShape1() {
        mBinding.AllRestaurantsFragmentRecyclerViewRestaurants.adapter = Adapter2(restaurants, this)
    }

    private fun updateUIShape2() {
        mBinding.AllRestaurantsFragmentRecyclerViewRestaurants.adapter = Adapter2(restaurants, this)
    }

    override fun setOnCreateViewHolder(parent: ViewGroup): ViewHolder2 {
        val layout = if (shape) R.layout.one_restaurant_shape1
        else R.layout.one_restaurant_shape2
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return VH(view, this)
    }

    override fun onClickRestaurant(restaurant: Restaurant) {
        TempStorage.instance().currentRestaurant = restaurant
        val intent = OneRestaurantActivity.instance(baseContext)
        startActivity(intent)
    }

}

class VH(viewItem: View, private val listener: ResListener) : ViewHolder2(viewItem) {
    private val logo =
        viewItem.findViewById<ImageView>(R.id.OneRestaurant_ImageView_RestaurantImage)
    private val name = viewItem.findViewById<TextView>(R.id.OneRestaurant_TextView_RestaurantName)
    private val deliveryTime =
        viewItem.findViewById<TextView>(R.id.OneRestaurant_TextView_RestaurantDeliveryAVGTime)
    private val deliveryCost =
        viewItem.findViewById<TextView>(R.id.OneRestaurant_TextView_RestaurantDeliveryAVGCost)
    private val mRating =
        viewItem.findViewById<TextView>(R.id.OneRestaurant_TextView_RestaurantRating)

    override fun bind(item: Any) {
        val restaurant = item as Restaurant
        Picasso.get().load(restaurant.logo).into(logo)
        name.text = restaurant.name
        deliveryTime.text = "15 mins"
        deliveryCost.text = "10 EGP"
        val rating = restaurant.rating(restaurant.rating)
        mRating.text = rating.title
        val icon = ResourcesCompat.getDrawable(itemView.resources, rating.image, null)
        mRating.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)

        itemView.setOnClickListener {
            listener.onClickRestaurant(restaurant)
        }
    }

}

interface ResListener {
    fun onClickRestaurant(restaurant: Restaurant)
}