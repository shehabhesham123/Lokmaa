package com.example.lokma.pojo.model

data class Order(
    val orderId: String?,
    val restaurantId: String,
    val userEmail: String,
    val items: List<Item>
) {

    fun fromObjToMap(): MutableMap<String, Any> {
        val data = mutableMapOf<String, Any>()
        data["RestaurantId"] = restaurantId
        data["UserEmail"] = userEmail
        val items: MutableList<MutableMap<String, Any>> = mutableListOf()
        for (i in this.items) {
            val foodId = i.food.id
            val quantity = i.quantity
            val item = mutableMapOf<String, Any>("FoodId" to foodId, "Quantity" to quantity)
            items.add(item)
        }
        data["Items"] = items
        return data
    }

}

