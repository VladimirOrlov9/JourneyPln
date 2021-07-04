package com.spbstu.journeypln.views

import android.content.Intent
import android.net.Uri
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface EditTripView: MvpView {

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun updateInfoAboutTrip(image: Uri, name: String, location: String,
                            duration: String, description: String, weight: Double)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun updateDestination(destination: String)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun startTakePictureIntent(intent: Intent)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun startTakePictureFromGalleryIntent(intent: Intent)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun updatePhoto(uri: Uri)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun updateDate(date: String)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun getBackByNavController()

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun hideAcceptFAB()

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun showToast(text: String)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun updateProgressLine(progress: Int)
}