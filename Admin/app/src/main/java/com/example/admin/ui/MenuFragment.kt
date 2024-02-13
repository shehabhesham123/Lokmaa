package com.example.admin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.admin.backend.firebase.Firestore
import com.example.admin.databinding.FragmentMenuBinding
import com.example.admin.pojo.Category
import com.example.admin.pojo.Menu
import com.example.admin.pojo.Restaurant
import com.example.admin.utils.Const
import com.example.admin.utils.TempStorage
import com.google.android.material.tabs.TabLayoutMediator

class MenuFragment : Fragment(), CategoryListener {
    private lateinit var mBinding: FragmentMenuBinding
    private lateinit var mFirestore: Firestore
    private var mRestaurant: Restaurant? = null
    private var mMenu: Menu? = null

    private lateinit var mCategoryFragments: MutableList<CategoryFragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirestore = Firestore(requireContext())
        mRestaurant = TempStorage.instance().restaurant
        mMenu = mRestaurant?.menu
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMenuBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpFragments()

        mBinding.viewpager2.adapter = Adapter(requireActivity(), mCategoryFragments)

        attachTabLayoutViewPager()

        mBinding.addCategory.setOnClickListener {
            val dialog = Dialog(this)
            dialog.show(parentFragmentManager, null)
        }
    }

    private fun setUpFragments() {
        mCategoryFragments =
            MutableList(mMenu!!.categories.size) { i -> CategoryFragment.instance(mMenu!!.categories[i].name) }
    }

    private fun attachTabLayoutViewPager() {
        TabLayoutMediator(
            mBinding.tabLayout, mBinding.viewpager2
        ) { tab, position ->
            tab.text = mMenu!!.categories[position].name
        }.attach()
    }

    override fun onAddCategory(name: String) {
        mBinding.progress.visibility = View.VISIBLE
        mBinding.addCategory.visibility = View.INVISIBLE
        val adapter = mBinding.viewpager2.adapter as Adapter
        mFirestore.update(mRestaurant!!, "${Const.RESTAURANT_PATH}/${mRestaurant!!.id}",
            {
                mBinding.progress.visibility = View.GONE
                mBinding.addCategory.visibility = View.VISIBLE
                mRestaurant?.menu?.add(Category(name))
                mCategoryFragments.add(CategoryFragment.instance(name))
                adapter.notifyItemInserted(mCategoryFragments.size - 1)
            }, {})
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


interface CategoryListener {
    fun onAddCategory(name: String)
}