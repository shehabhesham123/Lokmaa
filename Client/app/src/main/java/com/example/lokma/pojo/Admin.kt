package  com.example.lokma.pojo

class Admin {
    lateinit var username: String     // identifier of admin obj
        private set
    var password: String? = null
        private set
    var phone: String? = null
        private set

    /**  this constructor is used when create new admin */
    constructor(username: String, password: String, phone: String) {
        this.username = username
        this.password = password
        this.phone = phone
    }

    /**  this constructor is used when login.
     *
     * but when login successfully , you must delete password value for security
     *
     * so you must call successfulLogin(String) and pass admin name to it */
    constructor(username: String, password: String) {
        this.username = username
        this.password = password
    }

    /** this empty constructor is used by firebase when convert data to Admin obj */
    constructor()

    /** this constructor is used when user forget her password */
    constructor(phone: String) {
        this.phone = phone
    }

    fun successfulLogin() {
        password = null
    }

    override fun toString(): String {
        return "Admin [username: $username,   password: $password,   phone: $phone]"
    }


}