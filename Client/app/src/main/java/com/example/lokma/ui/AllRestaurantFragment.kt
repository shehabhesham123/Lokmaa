package com.example.lokma.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lokma.R
import com.example.lokma.databinding.FragmentAllRestaurantBinding
import com.example.lokma.firebase.Firestore
import com.example.lokma.pojo.adapter.RestaurantAdapter
import com.example.lokma.pojo.constant.Constant
import com.example.lokma.pojo.model.Restaurant
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

private const val USER_EMAIL_KEY = "UserEmail"

class AllRestaurantFragment() : Fragment() {

    private lateinit var binding: FragmentAllRestaurantBinding
    private lateinit var firestore: Firestore

    private lateinit var adapterShape1: RestaurantAdapter
    private lateinit var adapterShape2: RestaurantAdapter

    private var restaurants: MutableList<Restaurant> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize firestore
        firestore = Firestore(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_restaurant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAllRestaurantBinding.bind(view)

        propertiesOfRecyclerView()
        listenerOfImeOptionOfSearchEditText()
        recyclerViewShapesListener()
        filtersListener()
        getAllRestaurant()
    }

    fun refresh(){
        getAllRestaurant()
    }

    private fun getAllRestaurant() {
        binding.AllRestaurantsFragmentRecyclerViewRestaurants.visibility = View.INVISIBLE
        binding.AllRestaurantsFragmentAnimationView.visibility = View.VISIBLE
        firestore.download(Constant.restaurantsPath, {
            restaurants.clear()
            for (i in it) {
                i.data?.let {
                    val restaurant = Restaurant.fromMapToObj(i.id, i.data)
                    restaurants.add(restaurant)
                }
            }
            adapterShape2.refresh()
            adapterShape1.refresh()
            binding.AllRestaurantsFragmentRecyclerViewRestaurants.visibility = View.VISIBLE
            binding.AllRestaurantsFragmentAnimationView.visibility = View.INVISIBLE
        }, {
            // failed
        })

    }

    private fun propertiesOfRecyclerView() {
        adapterShape1 = RestaurantAdapter(restaurants, RestaurantAdapter.Shape.SHAPE1)
        adapterShape2 = RestaurantAdapter(restaurants, RestaurantAdapter.Shape.SHAPE2)
        binding.AllRestaurantsFragmentRecyclerViewRestaurants.adapter = adapterShape1 // as Default
        binding.AllRestaurantsFragmentRecyclerViewRestaurants.layoutManager =
            LinearLayoutManager(requireContext())
        binding.AllRestaurantsFragmentRecyclerViewRestaurants.hasFixedSize()
    }

    private fun listenerOfImeOptionOfSearchEditText() {
        val searchLayout =
            requireView().findViewById<TextInputLayout>(R.id.AllRestaurantsFragment_Include_SearchLayout)
        val search = searchLayout.findViewById<TextInputEditText>(R.id.SearchLayout_TextET_Search)
        search.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                val searchPhrase = search.text.toString()

                dataOfSearch(getRestaurantsThatMatchWithSearchPhrase(searchPhrase))
            }
            // to close keyboard after click on search icon at keyboard
            UIUtil.hideKeyboard(requireActivity())
            false
        }
    }

    private fun getRestaurantsThatMatchWithSearchPhrase(searchPhrase: String): MutableList<Restaurant> {
        val newList = mutableListOf<Restaurant>()
        for (i in restaurants) {
            if (i.name.contains(searchPhrase)) newList.add(i)
        }
        return newList
    }

    private fun dataOfSearch(data: MutableList<Restaurant>) {
        adapterShape1.changeList(data)
        adapterShape2.changeList(data)
        if (data.isEmpty()) {
            binding.AllRestaurantsFragmentAnimationViewNoData.visibility = View.VISIBLE
        } else {
            binding.AllRestaurantsFragmentAnimationViewNoData.visibility = View.INVISIBLE
        }
    }

    private fun recyclerViewShapesListener() {

        binding.AllRestaurantsFragmentRadioButtonShape1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.AllRestaurantsFragmentRadioButtonShape2.isChecked = false
                changeRecyclerViewShape(RestaurantAdapter.Shape.SHAPE1)
            }
        }
        binding.AllRestaurantsFragmentRadioButtonShape2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.AllRestaurantsFragmentRadioButtonShape1.isChecked = false
                changeRecyclerViewShape(RestaurantAdapter.Shape.SHAPE2)
            }
        }
    }

    private fun changeRecyclerViewShape(shape: RestaurantAdapter.Shape) {
        if (shape == RestaurantAdapter.Shape.SHAPE1) binding.AllRestaurantsFragmentRecyclerViewRestaurants.adapter =
            adapterShape1
        else binding.AllRestaurantsFragmentRecyclerViewRestaurants.adapter = adapterShape2
    }

    private fun filtersListener() {
        binding.AllRestaurantsFragmentTextViewFilters.setOnClickListener {
            showFilters()
        }
    }

    private fun showFilters() {

    }

}