package  com.example.delivery.pojo

import kotlin.properties.Delegates

class Category : Addition<Meal> {
    var name by Delegates.notNull<String>()
        private set
    var meals: MutableList<Meal> = mutableListOf()
        private set

    /** this constructor is used when the meals list is already ready */
    constructor(name: String, meals: MutableList<Meal>) {
        this.name = name
        this.meals = meals
    }

    /** this constructor is used when the meals list is not ready and the meals will be added later */
    constructor(name: String) {
        this.name = name
    }

    /** this empty constructor is used by firebase when convert data to Category obj */
    constructor()

    override fun add(item: Meal) {
        this.meals.add(item)
    }

    override fun toString(): String {
        return "Category [name: $name,   meals: $meals]"
    }
}