package  com.example.lokma.pojo

import kotlin.properties.Delegates

class OrderItem {
    var meal by Delegates.notNull<Meal>()
        private set
    var quantity = 1

    var type by Delegates.notNull<Meal.Type>()


    /**
     * this constructor is used to create new orderItem
     */
    constructor(meal: Meal, quantity: Int, type: Meal.Type) {
        this.meal = meal
        this.quantity = quantity
        this.type = type
    }

    constructor(meal: Meal, type: Meal.Type) {
        this.meal = meal
        this.type = type
    }


    /**
     * this empty constructor is used by firebase when convert data to OrderItem obj
     */
    constructor()

}