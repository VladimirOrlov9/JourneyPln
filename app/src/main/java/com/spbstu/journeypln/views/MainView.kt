package com.spbstu.journeypln.views

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface MainView: MvpView {

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun startCurrentTripPage(key: String)
}