package com.spbstu.journeypln.model.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.spbstu.journeypln.R
import com.spbstu.journeypln.adapters.ThisTripPagerAdapter
import com.spbstu.journeypln.model.activities.MainActivity
import moxy.MvpAppCompatFragment

class ThisTripFragment : MvpAppCompatFragment() {
    private lateinit var tripId: String

    private lateinit var thisTripPagerAdapter: ThisTripPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_this_trip, container, false)
        init(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        thisTripPagerAdapter = ThisTripPagerAdapter(this, tripId)
        viewPager.adapter = thisTripPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "О поездке"
                1 -> tab.text = "Вещи"
                2 -> tab.text = "Заметки"
            }
        }.attach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        val id = bundle?.getString("id")
        if (id != null) {
            this.tripId = id
        }

        (activity as MainActivity).supportActionBar?.title = "Информация о поездке"
    }

    private fun init(view: View) {

        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.sliding_tabs)
    }
}