package  com.example.admin.pojo

import kotlin.properties.Delegates

class Delivery {

    var username by Delegates.notNull<String>()
        private set
    var password: String? = null
        private set
    var phone: String? = null
        private set

    /**  this constructor is used when create new delivery */
    constructor(username: String, password: String, phone: String) {
        this.username = username
        this.password = password
        this.phone = phone
    }

    /**  this constructor is used when login.
     *
     * but when login successfully , you must delete password value for security
     *
     * so you must call successfulLogin(String) and pass delivery name to it */
    constructor(username: String, password: String) {
        this.username = username
        this.password = password
    }

    /** this constructor is used when user forget her password */
    constructor(phone: String) {
        this.phone = phone
    }

    fun successfulLogin() {
        password = null
    }


    /** this empty constructor is used by firebase when convert data to Delivery obj */
    constructor()

}