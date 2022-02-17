package com.spbstu.journeypln.presenters.fragmentPresenters

import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.data.room.entities.Trip
import com.spbstu.journeypln.views.AboutTripView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import java.text.SimpleDateFormat
import java.util.*

class AboutTripPresenter: MvpPresenter<AboutTripView>() {

    private var currentTrip: Trip? = null

    private lateinit var db: TripsDb

    private val outputDateFormat = SimpleDateFormat("EEE, d MMMM yyyy года", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun initDb(db: TripsDb) {
        this.db = db
    }

    fun getInfoAboutTrip(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val tripsDao = db.tripsDao()
            currentTrip = tripsDao.findTripById(id)
            if (currentTrip != null) {
                val trip = currentTrip!!

                val durationFrom = outputDateFormat.format(trip.startDate)
                val durationTo = outputDateFormat.format(trip.endDate)

                launch(Dispatchers.Main) {
                    viewState.updateInfoAboutTrip(
                        image = trip.imageUri, name = trip.name, location = trip.placeName,
                        durationFrom = durationFrom, durationTo = durationTo,
                        description = trip.description
                    )
                    getPackingSummary(id)
                    getTodoSummary(id)
                }
            }
        }
    }

    fun editTrip() {
        if (currentTrip != null) {
            viewState.pressEditTripButton(currentTrip!!.uid)
        } else {
            viewState.showToast("Секундочку")
        }
    }

    private fun getPackingSummary(id: Long) {

        CoroutineScope(Dispatchers.IO).launch {
            val clothesDao = db.clothesDao()
            val checkedNumberOfClothes = clothesDao.countCheckedClothes(id)
            val absoluteNumberOfClothes = clothesDao.countAbsoluteNumberOfClothes(id)
            val absoluteWeight = currentTrip!!.weight
            val takenWeight = clothesDao.countTakenWeight(id)

            println(takenWeight)

            launch(Dispatchers.Main) {
                viewState.updateProgressChecked(
                    checked = checkedNumberOfClothes,
                    sum = absoluteNumberOfClothes,
                    weightSum = absoluteWeight,
                    weightChecked = takenWeight
                )
            }
        }
    }

    private fun getTodoSummary(id: Long) {

        CoroutineScope(Dispatchers.IO).launch {
            val todoDao = db.todoDao()
            val sumClothes = todoDao.countAbsoluteNumberOfTasks(id)
            val sumChecked = todoDao.countNumberOfCheckedTasks(id)

            launch(Dispatchers.Main) {
                viewState.updateProgressTodoChecked(
                    checked = sumChecked,
                    sum = sumClothes
                )
            }
        }
    }

}