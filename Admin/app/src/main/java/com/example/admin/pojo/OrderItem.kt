package  com.example.admin.pojo

import kotlin.properties.Delegates

class OrderItem {
    var meal by Delegates.notNull<Meal>()
        private set
    var quantity by Delegates.notNull<Int>()
        private set
    var type by Delegates.notNull<Meal.Type>()
        private set

    /**
     * this constructor is used to create new orderItem
     */
    constructor(meal: Meal, quantity: Int, type: Meal.Type) {
        this.meal = meal
        this.quantity = quantity
        this.type = type
    }

    /**
     * this empty constructor is used by firebase when convert data to OrderItem obj
     */
    constructor()

}