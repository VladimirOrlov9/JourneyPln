package com.spbstu.journeypln.presenters.fragmentPresenters

import android.net.Uri
import android.view.View
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.views.TripsView
import kotlinx.coroutines.*
import moxy.MvpPresenter
import java.text.SimpleDateFormat
import java.util.*

class TripsPresenter: MvpPresenter<TripsView>() {

    private var closestTripKey: Long = 0
    private var lastTripKey: Long = 0
    private var currentTripKey: Long = 0

    private lateinit var db: TripsDb

    val outputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun setDB(db: TripsDb) {
        this.db = db
    }

    fun initLastTrip() {

        CoroutineScope(Dispatchers.IO).launch {
            val tripsDao = db.tripsDao()

            val id = tripsDao.findLast(System.currentTimeMillis())

            if (id != null) {
                lastTripKey = id.uid

                val startDate = outputDateFormat.format(id.startDate)
                val endDate = outputDateFormat.format(id.endDate)
                val duration = "$startDate - $endDate"

                withContext(Dispatchers.Main) {
                    viewState.boundLastTrip(
                        id.name,
                        id.imageUri,
                        id.placeName,
                        duration,
                        id.description
                    )

                    viewState.setLastTripVisibility(View.VISIBLE)
                }
            }
        }
    }

    fun initClosestTrip() {

        CoroutineScope(Dispatchers.IO).launch {
            val tripsDao = db.tripsDao()
            val id = tripsDao.findClosest(System.currentTimeMillis())

            if (id != null) {
                closestTripKey = id.uid

                val startDate = outputDateFormat.format(id.startDate)
                val endDate = outputDateFormat.format(id.endDate)
                val duration = "$startDate - $endDate"

                launch(Dispatchers.Main) {
                    viewState.boundClosestTrip(
                        id.name,
                        Uri.parse(id.imageUri),
                        id.placeName,
                        duration,
                        id.description
                    )

                    viewState.setClosestTripVisibility(View.VISIBLE)
                }
            }
        }
    }

    fun openClosestTrip() {
        viewState.openTrip(closestTripKey)
    }

    fun openLastTrip() {
        viewState.openTrip(lastTripKey)
    }

    fun openCurrentTrip() {
        viewState.openTrip(currentTripKey)
    }

    fun initCurrentTrip() {

        CoroutineScope(Dispatchers.IO).launch {
            val tripsDao = db.tripsDao()

            val id = tripsDao.findCurrent(System.currentTimeMillis())

            if (id != null) {
                currentTripKey = id.uid

                val startDate = outputDateFormat.format(id.startDate)
                val endDate = outputDateFormat.format(id.endDate)
                val duration = "$startDate - $endDate"

                launch(Dispatchers.Main) {
                    viewState.boundCurrentTrip(
                        id.name,
                        Uri.parse(id.imageUri),
                        id.placeName,
                        duration,
                        id.description
                    )

                    viewState.setCurrentTripVisibility(View.VISIBLE)
                }
            }
        }
    }
}