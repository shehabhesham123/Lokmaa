package  com.example.delivery.pojo

import com.example.delivery.utils.Rating
import kotlin.properties.Delegates

class Restaurant {
    var id: String? = null          // identifier of restaurant obj
        /**
         * When can a value be assigned to id ?
         *
         * When creating a new meal, it will be without an id.
         * so, must take the id value of the meal from the databases after registering it.
         */
        set(value) {
            value?.run {
                if (isNotEmpty() && isNotBlank())
                    field = value
            }
        }

    var name by Delegates.notNull<String>()
        private set
    var logo: String? = null    // logo variable is url of image
        set(value) {
            value?.run {
                if (isNotEmpty() && isNotBlank())
                    field = value
            }
        }
    var address by Delegates.notNull<Address>()
        private set
    var rating: Int = Rating.NORMAL   // as default
        private set
    var menu: Menu? = null
        private set
    var admin: Admin? = null

    /** this constructor is used when create new restaurant */
    constructor(name: String, logo: String, address: Address, emptyMenu: Menu, admin: Admin) {
        this.name = name
        this.logo = logo
        this.address = address
        this.menu = emptyMenu
        this.admin = admin
    }

    /** this constructor is used when get a restaurant from database */
    constructor(
        id: String,
        name: String,
        logo: String,
        address: Address,
        rating: Int,
        menu: Menu,
        admin: Admin
    ) {
        this.id = id
        this.name = name
        this.logo = logo
        this.address = address
        this.rating = rating
        this.menu = menu
        this.admin = admin
    }

    /** this empty constructor is used by firebase when convert data to Admin obj */
    constructor()
}