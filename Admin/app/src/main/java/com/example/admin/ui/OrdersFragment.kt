package com.example.admin.ui

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.backend.firebase.Firestore
import com.example.admin.databinding.FragmentOrdersBinding
import com.example.admin.pojo.Order
import com.example.admin.pojo.Restaurant
import com.example.admin.utils.Const
import com.example.admin.utils.OrderState
import com.example.admin.utils.TempStorage
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.tapadoo.alerter.Alerter


class OrdersFragment : Fragment(), OrderView, OrderListener, ViewHolder {

    private lateinit var mBinding: FragmentOrdersBinding
    private var mSnapshot: ListenerRegistration? = null
    private var mRestaurant: Restaurant? = null
    private var orders: MutableList<Order> = mutableListOf()
    private lateinit var mFirestore: Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirestore = Firestore(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentOrdersBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRestaurant = TempStorage.instance().restaurant

        val ordersPresenter = OrdersPresenter(this)
        ordersPresenter.getOrders(requireContext(), mRestaurant!!.id!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSnapshot?.remove()
    }

    override fun onGetOrders(orders: MutableList<Order>) {
        if (orders.isNotEmpty()) mBinding.no.visibility = View.GONE
        this.orders = orders
        addSnapShotToOrders()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        mBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mBinding.recyclerView.adapter = Adapter2(orders, this)
        mBinding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        attachItemTouchToRecycler()
    }

    private fun attachItemTouchToRecycler() {
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                val order = orders[viewHolder.adapterPosition]
                if (order.state == OrderState.PREPARE) {
                    if (dX > 0) {
                        val background = ResourcesCompat.getDrawable(resources, R.drawable.l1, null)
                        background!!.setBounds(
                            viewHolder.itemView.left,
                            viewHolder.itemView.top,
                            viewHolder.itemView.right + dX.toInt(),
                            viewHolder.itemView.bottom
                        )
                        background.draw(c)
                    } else {
                        val background = ResourcesCompat.getDrawable(resources, R.drawable.l2, null)
                        background!!.setBounds(
                            viewHolder.itemView.left + dX.toInt(),
                            viewHolder.itemView.top,
                            viewHolder.itemView.right,
                            viewHolder.itemView.bottom
                        )
                        background.draw(c)
                    }
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val order = orders[position]
                if (order.state != OrderState.PREPARE) {
                    mBinding.recyclerView.adapter?.notifyItemChanged(position)
                } else {
                    if (direction == ItemTouchHelper.LEFT)
                        order.state = OrderState.ACCEPTED
                    else
                        order.state = OrderState.CANCELED

                    mFirestore.update(order, "${Const.ordersPath(mRestaurant?.id!!)}/${order.id}", {
                        mBinding.recyclerView.adapter?.notifyItemChanged(position)
                    }, {})
                }
            }
        }).attachToRecyclerView(mBinding.recyclerView)
    }

    private fun addSnapShotToOrders() {
        mRestaurant?.run {
            mSnapshot = mFirestore.addSnapshot(Const.ordersPath(id!!), {
                for (i in it) {
                    val order = i.obj.toObject(Order::class.java)
                    if (i.whatHappened == DocumentChange.Type.ADDED && order.state == OrderState.NOTIFICATION_NOT_RECEIVE_TO_DELIVERY) {
                        order.state = OrderState.PREPARE
                        orders.add(order)
                        mFirestore.update(
                            order,
                            "${Const.ordersPath(mRestaurant?.id!!)}/${order.id}",
                            {
                                createAlerter()
                                mBinding.recyclerView.adapter?.notifyItemInserted(orders.size - 1)
                                mBinding.no.visibility = View.GONE
                            }, {})
                    }
                }
            }, {

            })
        }
    }

    private fun createAlerter() {
        Alerter.create(requireActivity())
            .setTitle("New orders is added")
            .setText("Take look to the new order")
            .setIcon(R.drawable.attention)
            .setDuration(2000)
            .enableSwipeToDismiss()
            .show()
    }

    override fun onClick(order: Order) {
        TempStorage.instance().order = order
        val bottomSheet = BottomSheet()
        bottomSheet.show(parentFragmentManager, null)
    }

    override fun setOnCreateViewHolder(parent: ViewGroup): ViewHolder2 {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.one_order, parent, false)
        return OrderHolder(view, this)
    }

}

class OrderHolder(itemView: View, private val listener: OrderListener) : ViewHolder2(itemView) {
    private val mTextView = itemView.findViewById<TextView>(R.id.OrderId)
    private val background = itemView.findViewById<FrameLayout>(R.id.background)

    override fun bind(item: Any) {
        val order = item as Order
        val resources = itemView.context.resources
        mTextView.text = resources.getString(R.string.Order, order.id)
        itemView.setOnClickListener {
            listener.onClick(order)
        }
        updateUI(order.state)
    }

    private fun updateUI(state: Int) {
        when (state) {
            OrderState.CANCELED -> {
                background.setBackgroundResource(R.color.red)
                mTextView.setTextColor(itemView.resources.getColor(R.color.primaryTextColor, null))
            }

            OrderState.ACCEPTED -> {
                background.setBackgroundResource(R.color.primaryColor)
                mTextView.setTextColor(itemView.resources.getColor(R.color.primaryTextColor, null))
            }

            else -> {
                background.setBackgroundResource(R.drawable.l3)
                mTextView.setTextColor(itemView.resources.getColor(R.color.primaryColor, null))
            }
        }
    }
}

interface OrderListener {
    fun onClick(order: Order)
}