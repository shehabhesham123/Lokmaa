package com.example.lokma.pojo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lokma.R
import com.example.lokma.pojo.listener.RestaurantListener
import com.example.lokma.pojo.model.Restaurant
import com.squareup.picasso.Picasso

class RestaurantAdapter(
    private var restaurants: MutableList<Restaurant>,
    private var recyclerViewShape: Shape
) :
    RecyclerView.Adapter<RestaurantAdapter.VH>() {

    private lateinit var listener: RestaurantListener

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (recyclerView.context is RestaurantListener) listener =
            recyclerView.context as RestaurantListener
        else throw Exception("You must implements from RestaurantListener")
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.OneRestaurant_ImageView_RestaurantImage)
        val name: TextView = itemView.findViewById(R.id.OneRestaurant_TextView_RestaurantName)
        val type: TextView = itemView.findViewById(R.id.OneRestaurant_TextView_RestaurantType)
        val rating: TextView = itemView.findViewById(R.id.OneRestaurant_TextView_RestaurantRating)
        val avgDeliveryTime: TextView =
            itemView.findViewById(R.id.OneRestaurant_TextView_RestaurantDeliveryAVGTime)
        val avgDeliveryCost: TextView =
            itemView.findViewById(R.id.OneRestaurant_TextView_RestaurantDeliveryAVGCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = getView(parent)
        return VH(view)
    }

    private fun getView(parent: ViewGroup): View {
        return if (recyclerViewShape == Shape.SHAPE1)
            LayoutInflater.from(parent.context)
                .inflate(R.layout.one_restaurant_shape1, parent, false)
        else
            LayoutInflater.from(parent.context)
                .inflate(R.layout.one_restaurant_shape2, parent, false)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val restaurant = restaurants[position]

        Picasso.get().load(restaurant.logo).into(holder.image)
        holder.name.text = restaurant.name
        holder.type.text = restaurant.type
        putRating(restaurant.rating, holder.rating)
        holder.avgDeliveryCost.text = restaurant.avgDeliveryCost()
        holder.avgDeliveryTime.text = restaurant.avgDeliveryTime

        holder.itemView.setOnClickListener {
            listener.setOnClickOnRestaurant(restaurant)
        }
    }

    private fun putRating(rating: Restaurant.Rating, ratingView: TextView) {
        ratingView.setCompoundDrawablesWithIntrinsicBounds(rating.icon, 0, 0, 0);
        ratingView.text = rating.name
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    fun changeList(newRestaurants: MutableList<Restaurant>) {
        this.restaurants = newRestaurants
        notifyItemRangeChanged(0, this.restaurants.size)
    }

    fun refresh(){
        notifyItemRangeChanged(0, this.restaurants.size)
    }

    enum class Shape {
        SHAPE1,
        SHAPE2
    }

}