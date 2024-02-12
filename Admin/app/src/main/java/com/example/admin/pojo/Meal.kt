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
    var image by Delegates.notNull<String>()

    var types: MutableList<Type> = mutableListOf()
        private set
    var rating: Int = Rating.NORMAL  // as default
        private set

    class Type {
        var name by Delegates.notNull<String>()   // for example ---> small , medium , large , xlarge
            private set
        var price by Delegates.notNull<Float>()
            private set

        constructor(name: String, price: Float){
            this.name = name
            this.price = price
        }
        constructor()
    }

    /** this constructor is used when get meals from database */
    constructor(id: String, name: String, image: String, types: MutableList<Type>) {
        this.id = id
        this.name = name
        this.image = image
        this.types = types
    }

    /** this constructor is used when the types list is already ready */
    constructor(name: String, image: String, types: MutableList<Type>) {
        this.name = name
        this.image = image
        this.types = types
    }

    /** this constructor is used when the types list is not ready and the types will be added later */
    constructor(name: String, image: String) {
        this.name = name
        this.image = image
    }

    /** this empty constructor is used by firebase when convert data to Location obj */
    constructor()

    override fun add(item: Type) {
        this.types.add(item)
    }

}
