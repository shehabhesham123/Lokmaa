package  com.example.admin.pojo

import com.example.admin.utils.OrderState
import kotlin.properties.Delegates

class OrderCanceled : Order {
    var reasons by Delegates.notNull<String>()
        private set
    var cancellationTime: String
        private set

    constructor (order: Order, reasons: String) : super(
        order.id,
        order.client,
        order.delivery,
	order.restaurant,
        order.date,
        order.items
    ) {
        this.reasons = reasons
    }

    constructor()

    init {
        super.state = OrderState.CANCELED
        cancellationTime = Date.currentDate
    }

}