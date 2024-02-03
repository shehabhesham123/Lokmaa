package  com.example.admin.pojo

import com.example.admin.utils.Rating
import kotlin.properties.Delegates

class Meal : Addition<Meal.Type> {
    var id: String? = null     // identifier of meal obj
        /**
        When can a value be assigned to id ?

        When creating a new meal, it will be without an id.
        so, must take the id value of the meal from the databases after registering it.
         */
        set(value) {
            value?.run {
                if (isNotEmpty() && isNotBlank())
                    field = value
            }

        }
    var name by Delegates.notNull<String>()
        private set
    var types: MutableList<Type> = mutableListOf()
        private set
    var rating: Int = Rating.NORMAL  // as default
        private set

    class Type(name: String, price: Float) {
        var name: String = name    // for example ---> small , medium , large , xlarge
            private set
        var price: Float = price
            private set

    }

    /** this constructor is used when get meals from database */
    constructor(id: String, name: String, types: MutableList<Type>) {
        this.id = id
        this.name = name
        this.types = types
    }

    /** this constructor is used when the types list is already ready */
    constructor(name: String, types: MutableList<Type>) {
        this.name = name
        this.types = types
    }

    /** this constructor is used when the types list is not ready and the types will be added later */
    constructor(name: String) {
        this.name = name
    }

    /** this empty constructor is used by firebase when convert data to Location obj */
    constructor()

    override fun add(item: Type) {
        this.types.add(item)
    }

}
