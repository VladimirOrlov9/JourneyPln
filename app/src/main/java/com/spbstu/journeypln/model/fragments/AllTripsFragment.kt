package com.spbstu.journeypln.model.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.spbstu.journeypln.R
import com.spbstu.journeypln.model.activities.MainActivity
import com.spbstu.journeypln.adapters.TripsPagerAdapter
import moxy.MvpAppCompatFragment

class AllTripsFragment : MvpAppCompatFragment() {

    private lateinit var tripsPagerAdapter: TripsPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_all_trips, container, false)
        init(view)

        return view
    }

    private fun init(view: View) {
        (activity as MainActivity).supportActionBar?.title = "Все поездки"

        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.sliding_tabs)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tripsPagerAdapter = TripsPagerAdapter(this)
        viewPager.adapter = tripsPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Ближайшие"
                1 -> tab.text = "Последние"
            }
        }.attach()
    }
}