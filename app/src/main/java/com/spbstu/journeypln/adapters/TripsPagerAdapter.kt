package com.spbstu.journeypln.adapters

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.spbstu.journeypln.model.fragments.TripsListFragment

class TripsPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = PAGES_NUM

    override fun createFragment(position: Int): Fragment {
        val fragment = TripsListFragment()
        val bundle = Bundle()
        bundle.putInt("pos", position)
        fragment.arguments = bundle
        return fragment
    }

    companion object {
        const val PAGES_NUM = 2
    }
}