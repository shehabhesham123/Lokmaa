package com.example.lokma.pojo

import com.example.lokma.constant.OrderState
import kotlin.properties.Delegates

/** only one constructor because this is admin app, it receives orders only*/
open class Order {
    var id by Delegates.notNull<String>()     // identifier of order obj
        private set
    var client by Delegates.notNull<Client>()
        private set
    var delivery by Delegates.notNull<Delivery>()
        private set
    var restaurant by Delegates.notNull<Restaurant>()
        private set
    var date by Delegates.notNull<String>()
        private set
    var items by Delegates.notNull<MutableList<OrderItem>>()
        private set
    var state: Int = OrderState.PREPARE


    /**
     * this constructor is used to receive on it data of order obj stored in database
     */
    constructor(
        id: String,
        client: Client,
        delivery: Delivery,
        restaurant: Restaurant,
        date: String,
        items: MutableList<OrderItem>
    ) {
        this.id = id
        this.client = client
        this.restaurant = restaurant
        this.date = date
        this.delivery = delivery
        this.items = items
    }

    /** this empty constructor is used by firebase when convert data to Order obj */
    constructor()
}

