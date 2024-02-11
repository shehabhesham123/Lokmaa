package com.example.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.admin.backend.firebase.Firestore
import com.example.admin.databinding.ActivityMenuBinding
import com.example.admin.pojo.Category
import com.example.admin.pojo.Restaurant
import com.example.admin.ui.CategoryFragment
import com.example.admin.ui.Dialog
import com.example.admin.utils.Const
import com.example.admin.utils.TempStorage
import com.google.android.material.tabs.TabLayoutMediator

class MenuActivity : AppCompatActivity(), AddCategoryListener {
    private lateinit var mBinding: ActivityMenuBinding
    private var restaurant: Restaurant? = null
    private lateinit var firestore: Firestore
    private lateinit var adapter: Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        firestore = Firestore(baseContext)
        firestore.download(
            Const.RESTAURANT_PATH,
            "admin.username",
            TempStorage.instance().admin!!.username, {
                if (it.size == 1) {
                    restaurant = it[0].toObject(Restaurant::class.java)
                    TempStorage.instance().restaurant = restaurant
                    Log.i("shehabhesham Err","$it")
                    val menu = restaurant!!.menu!!
                    val fragments =
                        MutableList(menu.categories.size) { i -> CategoryFragment.instance(menu.categories[i].name) }
                    adapter = Adapter(this, fragments)
                    mBinding.viewpager2.adapter = adapter
                    attachTabLayoutViewPager()
                }
            }, {
                Log.i("shehabhesham Err",it)
            }
        )

        mBinding.addCategory.setOnClickListener {
            val dialog = Dialog()
            dialog.show(supportFragmentManager, null)
        }

    }

    private fun attachTabLayoutViewPager() {
        TabLayoutMediator(
            mBinding.tabLayout, mBinding.viewpager2
        ) { tab, position ->
            tab.text = restaurant!!.menu!!.categories[position].name
            Log.i("aaa", "${tab.text}")
        }.attach()
    }

    companion object {
        fun instance(context: Context): Intent {
            return Intent(context, MenuActivity::class.java)
        }
    }

    override fun setOnPositionClickListener(name: String) {
        restaurant?.menu?.add(Category(name))
        adapter.fragments.add(CategoryFragment.instance(name))
        adapter.notifyItemInserted(adapter.fragments.size - 1)
        firestore.update(restaurant!!, "${Const.RESTAURANT_PATH}/${restaurant!!.id}", {}, {})
    }
}

class Adapter(
    fragmentActivity: FragmentActivity,
    val fragments: MutableList<CategoryFragment>
) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}

interface AddCategoryListener {
    fun setOnPositionClickListener(name: String)
}