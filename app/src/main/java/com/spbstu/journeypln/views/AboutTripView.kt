package com.spbstu.journeypln.views

import android.net.Uri
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface AboutTripView: MvpView {
    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun showToast(text: String)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun updateInfoAboutTrip(image: Uri, name: String, location: String, durationFrom: String,
                            durationTo: String, description: String)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun updateTemp(temp: String, minTemp: String, maxTemp: String, sunrise: String, sunset: String, humidity: String)

    @StateStrategyType(value = SkipStrategy::class)
    fun pressEditTripButton(key: String)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun updateProgressChecked(checked: Int, sum: Int, weightSum: Double, weightChecked: Double)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun updateProgressTodoChecked(checked: Int, sum: Int)
}