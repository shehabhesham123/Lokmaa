package  com.example.admin.pojo

interface Addition<T> {
    /**  common action between more than one model(class),
    and the difference between them is the type of param  */
    fun add(item: T)
}