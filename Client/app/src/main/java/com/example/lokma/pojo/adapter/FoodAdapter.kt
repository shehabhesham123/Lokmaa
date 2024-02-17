package com.example.lokma.pojo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lokma.R
import com.example.lokma.pojo.listener.AddToCartListener
import com.example.lokma.pojo.listener.AddingListener
import com.example.lokma.pojo.listener.FoodListener
import com.example.lokma.pojo.model.Food
import com.example.lokma.pojo.model.Item
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso

class FoodAdapter(private val foods: MutableList<Food>, private val listener: FoodListener) :
    RecyclerView.Adapter<FoodAdapter.VH>() {

    private lateinit var context: Context
    private lateinit var sauceAdapter: AddingAdapter
    private lateinit var addingTopAdapter: AddingAdapter
    private lateinit var addToCartListener: AddToCartListener

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.OneFood_ImageView_FoodImage)
        val name: TextView = itemView.findViewById(R.id.OneFood_TextView_FoodName)
        val price: TextView = itemView.findViewById(R.id.OneFood_TextView_FoodPrice)
        val rating: RatingBar = itemView.findViewById(R.id.OneFood_TextView_FoodRating)

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.context = recyclerView.context
        if (context is AddToCartListener) addToCartListener = context as AddToCartListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.one_food, parent, false)
        return VH(view)
    }

    private lateinit var item: Item
    private lateinit var addToCart: Button
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val food = foods[position]
        Picasso.get().load(food.imageUrl).into(holder.image)
        holder.name.text = food.name
        holder.price.text = "${food.price} EGP"
        holder.rating.rating = food.rating / 5
        holder.itemView.setOnClickListener {
            val item = Item(food, 1)
            showBottomSheet(item)
        }
    }

    override fun getItemCount(): Int {
        return foods.size
    }


    @SuppressLint("SetTextI18n")
    private fun showBottomSheet(item: Item) {
        val food = item.food
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, null)
        val bottomImage = sheetView.findViewById<ImageView>(R.id.bottom_image)
        val bottomFoodName = sheetView.findViewById<TextView>(R.id.bottom_tv_foodName)
        val bottomFoodPrice = sheetView.findViewById<TextView>(R.id.bottom_tv_total_price)
        val bottomSauce = sheetView.findViewById<RecyclerView>(R.id.bottom_RecyclerView_Sauce)
        val bottomAddTopping =
            sheetView.findViewById<RecyclerView>(R.id.bottom_RecyclerView_AddTopping)
        val decrease = sheetView.findViewById<ImageButton>(R.id.bottom_btn_subtract)
        val increase = sheetView.findViewById<ImageButton>(R.id.bottom_btn_add)
        val quantity = sheetView.findViewById<TextView>(R.id.bottom_tv_quantity)
        val addToCart = sheetView.findViewById<Button>(R.id.bottom_btn_add_to_cart)
        this.item = item
        this.addToCart = addToCart
        bottomFoodName.text = food.name
        bottomFoodPrice.text = food.price.toString()
        Picasso.get().load(food.imageUrl).into(bottomImage)

        setupSauceRecyclers(bottomSauce, food.sauce, food)
        setupAddToppingRecyclers(bottomAddTopping, food.addTopping, food)

        bottomSheetDialog.setContentView(sheetView)
        bottomSheetDialog.show()

        sheetView.findViewById<Button>(R.id.bottom_btn_add_to_cart).setOnClickListener {
            listener.setOnClickOnFood(food)
            bottomSheetDialog.dismiss()
        }
        addToCart.text = "Add to cart(${item.food.price})"
        decrease.setOnClickListener {
            if (item.quantity > 1)
                item.quantity--
            quantity.text = "${item.quantity}"
            price(item, addToCart)
        }

        increase.setOnClickListener {
            item.quantity++
            quantity.text = "${item.quantity}"
            price(item, addToCart)
        }

        addToCart.setOnClickListener {
            Log.i("aaa","a")
            addToCartListener.setOnClickOnAddToCart(item)
            bottomSheetDialog.dismiss()
        }
    }




    @SuppressLint("SetTextI18n")
    fun price(item: Item, addToCart: Button) {
        this.item = item
        this.addToCart = addToCart
        val price = item.quantity * item.food.price
        addToCart.text = "Add to cart($price)"
    }

    private fun setupSauceRecyclers(
        recyclerView: RecyclerView,
        data: List<Food.Adding>,
        food: Food
    ) {
        sauceAdapter = AddingAdapter(data, food, object : AddingListener {
            override fun setOnSelectAddingItem() {
                refresh()
            }
        })
        recyclerView.adapter = sauceAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.hasFixedSize()
    }

    private fun setupAddToppingRecyclers(
        recyclerView: RecyclerView,
        data: List<Food.Adding>,
        food: Food
    ) {
        addingTopAdapter = AddingAdapter(data, food, object : AddingListener {
            override fun setOnSelectAddingItem() {
                refresh()
            }
        })
        recyclerView.adapter = addingTopAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.hasFixedSize()
    }

    fun refresh() {
        price(this.item, this.addToCart)
    }

}