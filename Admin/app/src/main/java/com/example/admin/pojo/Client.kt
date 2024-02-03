package  com.example.admin.pojo

import kotlin.properties.Delegates

class Client {
    var name by Delegates.notNull<String>()
        private set
    var address by Delegates.notNull<Address>()
        private set
    var phone by Delegates.notNull<String>()
        private set

    /** this constructor is used to receive client obj from database */
    constructor(name: String, address: Address, phone: String) {
        this.name = name
        this.address = address
        this.phone = phone
    }

    /** this empty constructor is used by firebase when convert data to Client obj */
    constructor()
}