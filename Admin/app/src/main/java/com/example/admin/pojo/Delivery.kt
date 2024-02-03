package  com.example.admin.pojo

import kotlin.properties.Delegates

class Delivery {

    var name by Delegates.notNull<String>()
        private set
    var address by Delegates.notNull<Address>()
        private set

    /** this constructor is used to receive delivery obj from database */
    constructor(name: String, address: Address) {
        this.name = name
        this.address = address
    }

    /** this empty constructor is used by firebase when convert data to Delivery obj */
    constructor()

}