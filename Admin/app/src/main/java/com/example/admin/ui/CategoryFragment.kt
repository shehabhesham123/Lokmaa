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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.backend.firebase.Firestore
import com.example.admin.backend.firebase.Storage
import com.example.admin.databinding.FragmentCategoryBinding
import com.example.admin.pojo.AddMealListener
import com.example.admin.pojo.Meal
import com.example.admin.utils.Const
import com.example.admin.utils.Rating
import com.example.admin.utils.TempStorage
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryFragment private constructor() : Fragment(), AddMealListener {


    private var categoryName: String? = null
    private lateinit var meals: MutableList<Meal>
    private lateinit var mBinding: FragmentCategoryBinding
    private lateinit var firestore: Firestore
    private lateinit var storage: Storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = Firestore(requireContext())
        storage = Storage(requireContext())

        arguments?.run {
            categoryName = getString(CATEGORY_NAME_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentCategoryBinding.inflate(inflater)
        mBinding.mealsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        meals = findMyMeals()
        mBinding.mealsRecyclerView.adapter = MealsAdapter(meals)
        mBinding.addNewMeal.setOnClickListener {
            Dialog2(this).show(parentFragmentManager, "")
        }
        return mBinding.root
    }


    private fun findMyMeals(): MutableList<Meal> {
        val res = TempStorage.instance().restaurant
        for (i in res!!.menu!!.categories) {
            if (i.name == categoryName)
                return i.meals
        }
        return mutableListOf()
    }

    override fun onClickDone(meal: Meal) {
        meals.add(meal)
        val res = TempStorage.instance().restaurant
        storage.upload(Uri.parse(meal.image),Const.MEAL_IMAGE_PATH,{
            meal.image = it
            meal.id = Firestore.documentId
            firestore.update(res!!,"${Const.RESTAURANT_PATH}/${res.id}",{
                mBinding.mealsRecyclerView.adapter?.notifyItemInserted(meals.size-1)
            },{})

        },{},{})

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

    class MealsAdapter(private var meals: MutableList<Meal>) :
        RecyclerView.Adapter<MealsAdapter.VH>() {
        class VH(view: View) : RecyclerView.ViewHolder(view) {
            private var mealImage: ImageView
            private var mealName: MaterialTextView
            private var mealRating: RatingBar
            private var mealTypes: RecyclerView

            init {
                mealImage = view.findViewById(R.id.OneFood_ImageView_FoodImage)
                mealName = view.findViewById(R.id.OneFood_TextView_FoodName)
                mealRating = view.findViewById(R.id.OneFood_TextView_FoodRating)
                mealTypes = view.findViewById(R.id.recyclerView)
                mealTypes.layoutManager = LinearLayoutManager(super.itemView.context,LinearLayoutManager.HORIZONTAL,false)
            }

            fun bind(meal: Meal) {
                Picasso.get().load(meal.image).into(mealImage)
                mealName.text = meal.name
                mealRating.rating = getRating(meal.rating)
                Log.i("shehabhesham","${meal.types.size}")
                mealTypes.adapter = TypeAdapter(meal.types)
            }

            private fun getRating(rating: Int): Float {
                return when (rating) {
                    Rating.BAD -> 0.2f
                    Rating.NORMAL -> 0.5f
                    Rating.GOOD -> 0.7f
                    Rating.VERY_GOOD -> 1f
                    else -> 0.0f
                }

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_meal, parent, false)
            return VH(view)
        }

        override fun getItemCount(): Int {
            return meals.size
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(meals[position])
        }
    }

    class TypeAdapter(var types:MutableList<Meal.Type>):RecyclerView.Adapter<TypeAdapter.VH>(){
        class VH(view:View):RecyclerView.ViewHolder(view){
            val name = view.findViewById<TextView>(R.id.name)
            val price = view.findViewById<TextView>(R.id.price)

            fun bind(type:Meal.Type){
                name.text = type.name
                price.text = "${type.price} EGP"
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.type_layout2,parent,false)
            return VH(view)
        }

        override fun getItemCount(): Int {
            return types.size
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(types[position])
        }
    }

}