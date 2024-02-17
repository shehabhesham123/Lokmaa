package com.example.lokma.pojo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.lokma.R
import com.example.lokma.pojo.listener.CartListener
import com.example.lokma.pojo.model.Cart
import com.example.lokma.pojo.model.Item
import com.squareup.picasso.Picasso

class CartAdapter(private val cart: Cart,private val listener: CartListener) : RecyclerView.Adapter<CartAdapter.VH>() {
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodImage: ImageView = itemView.findViewById(R.id.OneCartItem_ImageView_FoodImage)
        val foodName: TextView = itemView.findViewById(R.id.OneCartItem_TextView_FoodName)
        val foodPrice: TextView = itemView.findViewById(R.id.OneCartItem_TextView_Price)
        val quantity: TextView = itemView.findViewById(R.id.OneCartItem_TextView_Quantity)
        val increase: CardView = itemView.findViewById(R.id.OneCartItem_CardView_AddQuantity)
        val decrease: CardView = itemView.findViewById(R.id.OneCartItem_CardView_ReduceQuantity)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.one_cart_item, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = cart.items[position]
        putFoodData(item, holder)

        holder.increase.setOnClickListener {
            val newQuantity = item.increaseQuantity()
            holder.quantity.text = newQuantity.toString()
            listener.setOnQuantityChangeListener()
        }

        holder.decrease.setOnClickListener {
            val newQuantity = item.decreaseQuantity()
            holder.quantity.text = newQuantity.toString()
            if (newQuantity == 0) {
                notifyItemRemoved(position)
                cart.items.remove(item)
            }
            listener.setOnQuantityChangeListener()
        }
    }

    override fun getItemCount(): Int {
        return cart.items.size
    }

    @SuppressLint("SetTextI18n")
    private fun putFoodData(item: Item, holder: VH) {
        Picasso.get().load(item.food.imageUrl).into(holder.foodImage)
        holder.foodName.text = item.food.name
        holder.foodPrice.text = "${item.food.price} EGP"
        holder.quantity.text = item.quantity.toString()
    }

}