package com.spbstu.journeypln.views

import com.spbstu.journeypln.adapters.TripsRecyclerAdapter
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface TripsListView: MvpView {

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun showToast(text: String)
    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun setUpAdapter(adapter: TripsRecyclerAdapter)
    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun showSnackBarDeletedItem(text: String)
    @StateStrategyType(value = SkipStrategy::class)
    fun openClickedTrip(id: String)
}