package com.example.lokma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lokma.R
import com.example.lokma.constant.TempStorage
import com.example.lokma.databinding.FragmentOneCategoryBinding
import com.example.lokma.pojo.Meal
import com.squareup.picasso.Picasso

class OneCategoryFragment : Fragment(), ViewHolder, MealListener {

    private lateinit var meals: MutableList<Meal>
    private lateinit var mBinding: FragmentOneCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.run {
            val categoryName = getString(PARAM1)
            TempStorage.instance().currentRestaurant?.menu?.categories?.let {
                for (i in it) {
                    if (i.name == categoryName) meals = i.meals
                }
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentOneCategoryBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.menuRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        mBinding.menuRecycler.hasFixedSize()
        mBinding.menuRecycler.adapter = Adapter2(meals, this)
    }

    companion object {
        private const val PARAM1 = "param1"
        fun instance(categoryName: String): OneCategoryFragment {
            val bundle = Bundle()
            bundle.putString(PARAM1, categoryName)
            val fragment = OneCategoryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun setOnCreateViewHolder(parent: ViewGroup): ViewHolder2 {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.one_food, parent, false)
        return MealHolder(view, this)
    }

    override fun onClickFood(meal: Meal) {
        // show meal
        TempStorage.instance().meal = meal
        val bottomSheet = BottomSheetFragment()
        bottomSheet.show(parentFragmentManager, null)
    }

}

class MealHolder(viewItem: View, private val listener: MealListener) : ViewHolder2(viewItem) {
    private val image = itemView.findViewById<ImageView>(R.id.OneFood_ImageView_FoodImage)
    private val name = itemView.findViewById<TextView>(R.id.OneFood_TextView_FoodName)
    private val price = itemView.findViewById<TextView>(R.id.OneFood_TextView_FoodPrice)
    private val rating = itemView.findViewById<RatingBar>(R.id.OneFood_TextView_FoodRating)
    override fun bind(item: Any, position: Int) {
        val meal = item as Meal
        name.text = meal.name
        price.text = itemView.resources.getString(R.string.price, meal.types[0].price.toString())
        Picasso.get().load(meal.image).into(image)
        rating.rating = meal.rating(meal.rating)

        itemView.setOnClickListener { listener.onClickFood(meal) }
    }
}

interface MealListener {
    fun onClickFood(meal: Meal)
}