package com.spbstu.journeypln.views

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface AboutTripView: MvpView {
    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun showToast(text: String)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun updateInfoAboutTrip(image: String, name: String, location: String, durationFrom: String,
                            durationTo: String, description: String)

    @StateStrategyType(value = SkipStrategy::class)
    fun pressEditTripButton(key: Long)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun updateProgressChecked(checked: Int, sum: Int, weightSum: Double, weightChecked: Double)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun updateProgressTodoChecked(checked: Int, sum: Int)
}