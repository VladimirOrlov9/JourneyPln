package com.spbstu.journeypln.model.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.spbstu.journeypln.R
import com.spbstu.journeypln.adapters.ThisTripPagerAdapter
import com.spbstu.journeypln.model.activities.MainActivity
import moxy.MvpAppCompatFragment
import kotlin.properties.Delegates

class ThisTripFragment : MvpAppCompatFragment() {
    private lateinit var thisTripPagerAdapter: ThisTripPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var toolbar: Toolbar

    private var tripId by Delegates.notNull<Long>()
    private lateinit var tripName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_this_trip, container, false)
        init(view)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)


        val navHostFragment = NavHostFragment.findNavController(this)
//        val appBarConfiguration = AppBarConfiguration(requireActivity().supportFragmentManager.primaryNavigationFragment..graph)
        NavigationUI.setupWithNavController(toolbar, navHostFragment)

//        toolbar.setupWithNavController(requireActivity().findNavController(R.id.my_nav))

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

    override fun onResume() {
        super.onResume()

        (requireActivity() as AppCompatActivity).supportActionBar?.title = tripName
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            this.tripId = bundle.getLong("id")
            val name = bundle.getString("name")
            if (name != null) {
                this.tripName = name
            }
        }
    }

    private fun init(view: View) {

        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.sliding_tabs)

        toolbar = view.findViewById(R.id.toolbar)
    }
}