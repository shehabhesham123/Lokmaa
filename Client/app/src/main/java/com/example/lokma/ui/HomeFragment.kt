package com.example.lokma.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lokma.R
import com.example.lokma.databinding.FragmentHomeBinding
import com.example.lokma.firebase.Firestore
import com.example.lokma.pojo.adapter.FoodAdapter
import com.example.lokma.pojo.constant.Constant
import com.example.lokma.pojo.listener.FoodListener
import com.example.lokma.pojo.listener.HomeListener
import com.example.lokma.pojo.model.*
import com.squareup.picasso.Picasso

private const val RESTAURANT_KEY = "Restaurant"
private const val USER_EMAIL_KEY = "UserEmail"

class HomeFragment private constructor() : Fragment() {

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: FragmentHomeBinding
    private lateinit var listener: HomeListener
    private lateinit var firestore: Firestore

    private lateinit var deliverTo: TextView
    private lateinit var address: TextView
    private lateinit var distance: TextView
    private lateinit var changeLocation: ImageView
    private lateinit var discountLayout: CardView
    private lateinit var discountImage: ImageView
    private lateinit var discountName: TextView
    private lateinit var discountPercentage: TextView
    private lateinit var discountOrderNow: Button

    private lateinit var discount: Discount
    private var topOfWeek = mutableListOf<Food>()

    private lateinit var topOfWeekAdapter: FoodAdapter

    private lateinit var restaurant: Restaurant
    private var userEmail: String? = null
    private var user: User? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeListener) listener = context
        else throw Exception("You must implements from HomeListener (HomeListener)")

        firestore = Firestore(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            restaurant = it.getSerializable(RESTAURANT_KEY) as Restaurant
            userEmail = it.getString(USER_EMAIL_KEY)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        defineViews(view)
        getTopOfWeek()
        getDiscount()
        getUserData()

        changeLocation.setOnClickListener {
            listener.setOnClickOnLocation()
        }

        discountOrderNow.setOnClickListener {
            listener.setOnClickOnDiscount(discount.food)
        }

    }

    private fun getTopOfWeek() {
        firestore.download(
            Constant.foodPath(restaurant.id),
            Constant.topFoodPath,
            10,
            {
                for (i in it)
                    i.data?.let {
                        topOfWeek.add(Food.fromMapToObj(i.id, i.data).first)
                    }
                propertiesOfRecycler()
            },
            {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            })
    }

    private fun getDiscount() {
        firestore.download(Constant.discountPath(restaurant.id), {
            if (it.isNotEmpty()) {
                it[0].data?.let { data ->
                    val foodId = data["FoodId"]?.toString()
                    val percentage = data["Percentage"].toString().toFloat()
                    firestore.downloadOneDocument("${Constant.foodPath(restaurant.id)}/$foodId",
                        { firestoreData ->
                            firestoreData.data?.let { data2 ->
                                val food = Food.fromMapToObj(firestoreData.id, data2)
                                discount = Discount(food.first, percentage)
                                putDiscountData()
                            }
                        },
                        { error ->
                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                        })
                }
            } else {
                discountLayout.visibility = View.GONE
            }
        }, {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
    }

    fun refresh(){
        getUserData()
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


    @SuppressLint("SetTextI18n")
    private fun putDiscountData() {
        Picasso.get().load(discount.food.imageUrl).into(discountImage)
        discountName.text = discount.food.name
        discountPercentage.text = "${discount.percentage}%"
    }

    private fun defineViews(view: View) {
        discountLayout = view.findViewById(R.id.Location_CardView_ContainerLocation)
        deliverTo = view.findViewById(R.id.Location_TextView_DeliveryTo)
        address = view.findViewById(R.id.Location_TextView_LocationAddress)
        distance = view.findViewById(R.id.Location_TextView_Distance)
        discountImage = view.findViewById(R.id.Discount_ImageView_DiscountImage)
        discountName = view.findViewById(R.id.Discount_TextView_DiscountName)
        discountPercentage = view.findViewById(R.id.Discount_TextView_DiscountPercentage)
        discountOrderNow = view.findViewById(R.id.Discount_Button_OrderNow)
        changeLocation = view.findViewById(R.id.Location_ImageView_ChangeLocation)
    }

    private fun propertiesOfRecycler() {
        topOfWeekAdapter = FoodAdapter(topOfWeek, object : FoodListener {
            override fun setOnClickOnFood(food: Food) {

            }
        })
        binding.HomeFragmentRecyclerViewTopOfWeek.adapter = topOfWeekAdapter
        binding.HomeFragmentRecyclerViewTopOfWeek.hasFixedSize()
        binding.HomeFragmentRecyclerViewTopOfWeek.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    companion object {
        fun instance(restaurant: Restaurant, userEmail: String?): HomeFragment {
            val bundle = Bundle()
            bundle.putSerializable(RESTAURANT_KEY, restaurant)
            bundle.putString(USER_EMAIL_KEY, userEmail)
            val homeFragment = HomeFragment()
            homeFragment.arguments = bundle
            return homeFragment
        }
    }
}
