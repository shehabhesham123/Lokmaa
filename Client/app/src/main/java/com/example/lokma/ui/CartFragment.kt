package com.example.lokma.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lokma.R
import com.example.lokma.databinding.FragmentCartBinding
import com.example.lokma.firebase.Firestore
import com.example.lokma.pojo.adapter.CartAdapter
import com.example.lokma.pojo.constant.Constant
import com.example.lokma.pojo.listener.CartListener
import com.example.lokma.pojo.listener.CartListener2
import com.example.lokma.pojo.model.*
import com.google.android.material.snackbar.Snackbar

private const val RESTAURANT_KEY = "RestaurantId"
private const val USER_EMAIL_KEY = "UserEmail"

class CartFragment private constructor() : Fragment(), CartListener {

    private lateinit var googleMap: GoogleMap

    private lateinit var deliverTo: TextView
    private lateinit var address: TextView
    private lateinit var distance: TextView
    private lateinit var changeLocation: ImageView
    private lateinit var adapter: CartAdapter

    private lateinit var binding: FragmentCartBinding
    private lateinit var firestore: Firestore

    private var cart: Cart? = null
    private var restaurantId: String? = null
    private lateinit var restaurant: Restaurant
    private var userEmail: String? = null
    private var user: User? = null

    private lateinit var listener: CartListener2

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CartListener2) listener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            restaurant = it.getSerializable(RESTAURANT_KEY) as Restaurant
            restaurantId = restaurant.id
            userEmail = it.getString(USER_EMAIL_KEY)
        }

        // initialize firestore
        firestore = Firestore(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onResume() {
        super.onResume()
        this.cart = Cart(restaurantId!!,listener.restore())
        propertiesRecyclerView()
        getUserData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCartBinding.bind(view)

        defineViews(view)

        getUserData()
        this.cart = Cart(restaurantId!!,listener.restore())
        propertiesRecyclerView()

        binding.CartFragmentButtonPleaseOrder.setOnClickListener {
            try {
                if (cart!!.items.isNotEmpty())
                    order()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        }

        changeLocation.setOnClickListener {
            listener.setOnClickOnChangeLocation()
        }

        binding.CartFragmentButtonPleaseOrder.setOnClickListener {
            val order = Order(System.currentTimeMillis().toString(),restaurantId!!,userEmail!!,cart!!.items)
            Log.i("aaaa","${order.orderId}")
            firestore.upload(order.fromObjToMap(),Constant.orderPath+"/${order.orderId}",{
                Toast.makeText(requireContext(), "Done", Toast.LENGTH_SHORT).show()
            },{

            })
        }


        binding.CartFragmentTextViewTotalPrice.text = getTotalPrice()
    }

    private fun getUserData() {
        userEmail?.let {
            firestore.downloadOneDocument(Constant.userPath(it), { firestoreDate ->
                firestoreDate.data?.let { data ->
                    user = User.fromMapToObj(firestoreDate.id, data)
                    // user don't have addresses
                    // so add default address
                    if (user?.address == null)
                        user?.address = Location(27.186398, 31.168523)
                }
                putLocationData()
            }, {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            })
        }
    }

    fun refresh() {
        getUserData()
    }

    fun refresh2(){
        this.cart =
            Cart(restaurantId!!, Cart.restoreDataFromLocalFile(restaurantId!!, requireContext()))
        getCartItems()
    }

    @SuppressLint("SetTextI18n")
    private fun putLocationData() {
        googleMap = GoogleMap(requireContext(), childFragmentManager, 0)
        if (Validation.isOnline(requireContext())) {
            distance.visibility = View.VISIBLE
            val location = user?.address
            location?.let {
                googleMap.getAddress(it.getLocation()) { address ->
                    this.address.text = address.shortAddress
                }
            }
            val distance = String.format(
                "%.2f", Location.distance(
                    location!!.latitude,
                    location.longitude,
                    restaurant.lat,
                    restaurant.long
                )
            )

            this.distance.text = "$distance km"
        } else {
            //distance.visibility = View.INVISIBLE
        }
    }

    private fun getCartItems() {
        restaurantId?.let {
            cart = Cart(it, Cart.restoreDataFromLocalFile(it, requireContext()))
        }
    }

    private fun propertiesRecyclerView() {
        adapter = CartAdapter(cart!!, this)
        binding.CartFragmentRecyclerViewOrders.adapter = adapter
        binding.CartFragmentRecyclerViewOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.CartFragmentRecyclerViewOrders.hasFixedSize()
    }

    private fun defineViews(view: View) {
        deliverTo = view.findViewById(R.id.Location_TextView_DeliveryTo)
        address = view.findViewById(R.id.Location_TextView_LocationAddress)
        distance = view.findViewById(R.id.Location_TextView_Distance)
        changeLocation = view.findViewById(R.id.Location_ImageView_ChangeLocation)
    }

    private fun getTotalPrice(): String {
        var totalPrice = 0f
        for (i in cart!!.items) {
            totalPrice += (i.quantity * i.food.price)
        }
        return " ${String.format("%.2f", totalPrice)} EGP"
    }

    private fun order() {
        if (Validation.isOnline(requireContext())) {
            try {
                val order = Order(null, restaurantId!!, userEmail!!, cart!!.items)
                firestore.upload(order.fromObjToMap(), Constant.orderPath, {
                    orderIsOrdered()
                }, {
                    Snackbar.make(
                        requireView(),
                        "An error occurred while ordering",
                        Snackbar.LENGTH_SHORT
                    ).show()
                })
            } catch (e: Exception) {
                Snackbar.make(
                    requireView(),
                    "An error occurred while ordering",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun orderIsOrdered() {
        try {
            cart?.deleteMyItems(requireContext())
            adapter.notifyItemRangeRemoved(0, cart!!.items.size)
            cart?.items?.clear()
            binding.CartFragmentTextViewTotalPrice.text = getTotalPrice()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun instance(restaurant: Restaurant, userEmail: String): CartFragment {
            val bundle = Bundle()
            bundle.putSerializable(RESTAURANT_KEY, restaurant)
            bundle.putString(USER_EMAIL_KEY, userEmail)
            val fragment = CartFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun setOnQuantityChangeListener() {
        binding.CartFragmentTextViewTotalPrice.text = getTotalPrice()
    }
}


// فى ال onRestaurant هيبقى فيه Cart obj وكل ما اضيف اكل اضيف فيه ال cart obj ولما اروح ع الصفحة دى امسح اللى ف ال file واكتب الجديد
