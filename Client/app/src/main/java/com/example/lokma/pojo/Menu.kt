package  com.example.lokma.pojo

class Menu : Addition<Category> {

    var categories: MutableList<Category> = mutableListOf()
        private set

    /** this constructor is used when the menu categories is already ready */
    constructor(categories: MutableList<Category>) {
        this.categories = categories
    }

    /** this constructor is used when create a new empty menu and then add its category
     *
     * or used by firebase when convert data to Menu obj */
    constructor()

    override fun add(item: Category) {
        categories.add(item)
    }
}