package com.spbstu.journeypln.views

import android.content.Intent
import android.net.Uri
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SingleStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface CreationNewTripView: MvpView {

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun setDestinationText(text: String)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun hideAcceptFAB()

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun showToast(text: String)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun getBackByNavController()

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun updateProgressLine(progress: Int)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun updateImage(img: Uri)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun setTripDate(date: String)

    @StateStrategyType(value = SkipStrategy::class)
    fun startTakePictureIntent(intent: Intent)

    @StateStrategyType(value = SkipStrategy::class)
    fun startTakePictureFromGalleryIntent(intent: Intent)

}