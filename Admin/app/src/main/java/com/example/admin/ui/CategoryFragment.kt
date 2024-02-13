package com.example.admin.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.admin.R
import com.example.admin.backend.firebase.Firestore
import com.example.admin.backend.firebase.Storage
import com.example.admin.databinding.FragmentCategoryBinding
import com.example.admin.pojo.AddMealListener
import com.example.admin.pojo.Category
import com.example.admin.pojo.Meal
import com.example.admin.utils.Const
import com.example.admin.utils.Rating
import com.example.admin.utils.TempStorage
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso

class CategoryFragment private constructor() : Fragment(), AddMealListener, ViewHolder,
    MealListener {

    private lateinit var mBinding: FragmentCategoryBinding

    private lateinit var mFirestore: Firestore
    private lateinit var mStorage: Storage

    private var mCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFirestore = Firestore(requireContext())
        mStorage = Storage(requireContext())

        arguments?.run {
            mCategory = getCategory(getString(CATEGORY_NAME_KEY))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCategoryBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        mBinding.addNewMeal.setOnClickListener {
            TempStorage.instance().meal = null
            Dialog2(this).show(parentFragmentManager, "")
        }
    }

    private fun setUpRecyclerView() {
        mBinding.mealsRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mBinding.mealsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                StaggeredGridLayoutManager.VERTICAL
            )
        )
        mBinding.mealsRecyclerView.adapter = Adapter2(mCategory!!.meals, this)
    }

    private fun getCategory(categoryName: String?): Category? {
        val res = TempStorage.instance().restaurant
        for (i in res!!.menu!!.categories) {
            if (i.name == categoryName)
                return i
        }
        return null
    }

    override fun onClickDone(meal: Meal) {
        mBinding.prgress.visibility = View.VISIBLE
        mBinding.addNewMeal.visibility = View.INVISIBLE

        val restaurant = TempStorage.instance().restaurant
        mStorage.upload(Uri.parse(meal.image), Const.MEAL_IMAGE_PATH, {
            for (i in restaurant!!.menu!!.categories){
                if (i.name == mCategory!!.name)
                    i.meals.add(meal)
            }

            meal.image = it
            meal.id = Firestore.documentId()

            Log.i("shehab","${meal.id}  ${meal.image}")
            mFirestore.update(restaurant, "${Const.RESTAURANT_PATH}/${restaurant.id}", {
                mBinding.prgress.visibility = View.GONE
                mBinding.addNewMeal.visibility = View.VISIBLE
                mBinding.mealsRecyclerView.adapter?.notifyItemInserted(mCategory!!.meals.size - 1)
            }, {})
        }, {}, {})
    }

    override fun setOnCreateViewHolder(parent: ViewGroup): ViewHolder2 {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(view, this)
    }

    override fun onClickOnMeal(meal: Meal) {
        TempStorage.instance().meal = meal
        Dialog2(this).show(parentFragmentManager, "")
    }

    companion object {
        private const val CATEGORY_NAME_KEY = "CategoryName"

        fun instance(categoryName: String): CategoryFragment {
            val bundle = Bundle()
            bundle.putString(CATEGORY_NAME_KEY, categoryName)
            val categoryFragment = CategoryFragment()
            categoryFragment.arguments = bundle
            return categoryFragment
        }
    }

    class MealViewHolder(viewItem: View, private val listener: MealListener) :
        ViewHolder2(viewItem), ViewHolder {
        private val mealImage: ImageView = viewItem.findViewById(R.id.OneFood_ImageView_FoodImage)
        private val mealName: MaterialTextView =
            viewItem.findViewById(R.id.OneFood_TextView_FoodName)
        private val mealRating: RatingBar = viewItem.findViewById(R.id.OneFood_TextView_FoodRating)
        private val mealTypes: RecyclerView = viewItem.findViewById(R.id.recyclerView)

        init {
            mealTypes.layoutManager = GridLayoutManager(
                super.itemView.context,
                2
            )
        }

        override fun bind(item: Any) {
            val meal = item as Meal
            Picasso.get().load(meal.image).into(mealImage)
            mealName.text = meal.name
            mealRating.rating = getRating(meal.rating)
            mealTypes.adapter = Adapter2(meal.types, this)

            itemView.setOnClickListener {
                listener.onClickOnMeal(meal)
            }
        }

        private fun getRating(rating: Int): Float {
            return when (rating) {
                Rating.BAD -> 1f
                Rating.NORMAL -> 2f
                Rating.GOOD -> 3f
                Rating.VERY_GOOD -> 4f
                else -> 0.0f
            }

        }

        override fun setOnCreateViewHolder(parent: ViewGroup): ViewHolder2 {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.type_layout2, parent, false)
            return TypeViewHolder(view)
        }
    }

    class TypeViewHolder(viewItem: View) : ViewHolder2(viewItem) {
        private val name = viewItem.findViewById<TextView>(R.id.name)
        private val price = viewItem.findViewById<TextView>(R.id.price)

        override fun bind(item: Any) {
            val type = item as Meal.Type
            name.text = type.name
            price.text = itemView.resources.getString(R.string.price, type.price.toString())
        }
    }

}

interface MealListener {
    fun onClickOnMeal(meal: Meal)
}