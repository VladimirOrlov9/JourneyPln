package com.spbstu.journeypln.views

import android.net.Uri
import android.view.View
import com.spbstu.journeypln.data.firebase.pojo.Trip
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import java.io.File

interface TripsView: MvpView {

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun setLastTripVisibility(visibility: Int)
    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun setClosestTripVisibility(visibility: Int)
    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun setCurrentTripVisibility(visibility: Int)
    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun boundLastTrip(name:String, imageUri: Uri, destination: String, duration: String, description: String)
    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun boundClosestTrip(name:String, imageUri: Uri, destination: String, duration: String, description: String)
    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun boundCurrentTrip(name:String, imageUri: Uri, destination: String, duration: String, description: String)
    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun showToast(text: String)
    @StateStrategyType(value = SkipStrategy::class)
    fun openTrip(id: Long)
}