package com.spbstu.journeypln.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.spbstu.journeypln.model.fragments.*

class ThisTripPagerAdapter(fragment: Fragment, val id: Long): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = PAGES_NUM

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putLong("id", id)

        return when (position) {
            0 -> {
                val fragment = AboutTripFragment()
                fragment.arguments = bundle
                fragment
            }
            1 -> {
                val fragment = ClothesFragment()
                fragment.arguments = bundle
                fragment
            }
//            2 -> {
//                val fragment = ToDoListFragment()
//                fragment.arguments = bundle
//                fragment
//            }
            else -> {
                val fragment = AboutTripFragment()
                fragment.arguments = bundle
                fragment
            }
        }

    }

    companion object {
        const val PAGES_NUM = 3
    }
}