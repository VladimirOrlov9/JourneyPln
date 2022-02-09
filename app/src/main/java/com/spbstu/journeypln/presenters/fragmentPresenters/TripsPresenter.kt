package com.spbstu.journeypln.presenters.fragmentPresenters

import android.net.Uri
import android.view.View
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.views.TripsView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import java.text.SimpleDateFormat
import java.util.*

class TripsPresenter: MvpPresenter<TripsView>() {

    private var closestTripKey: Long = 0
    private var lastTripKey: Long = 0
    private var currentTripKey: Long = 0

    private lateinit var db: TripsDb

    private val outputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun setDB(db: TripsDb) {
        this.db = db
    }

    fun initLastTrip() {

        GlobalScope.launch {
            launch(Dispatchers.IO) {
                val tripsDao = db.tripsDao()

                val id = tripsDao.findLast(System.currentTimeMillis())

                if (id != null) {
                    lastTripKey = id.uid

                    val startDate = outputDateFormat.format(id.startDate)
                    val endDate = outputDateFormat.format(id.endDate)
                    val duration = "$startDate - $endDate"

                    launch(Dispatchers.Main) {
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

//        val databaseReference = database.getReference("users/${signInAccount.uid}")
//
//        databaseReference.orderByChild("endDate").addValueEventListener(
//                object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if (snapshot.exists()) {
//                            val uploadList = snapshot.children
//                                    .filter { snap ->
//                                        val value = snap.child("endDate").value as Long?
//                                        return@filter if (value != null) {
//                                            value < System.currentTimeMillis()
//                                        } else {
//                                            false
//                                        }
//                                    }
//                            if (uploadList.isNotEmpty()) {
//                                viewState.setLastTripVisibility(View.VISIBLE)
//
//                                val upload = uploadList.last().getValue(Trip::class.java)
//                                lastTripKey = uploadList.last().key.toString()
//
//                                if (upload != null) {
//                                    val name = upload.name
//                                    val imageUri = Uri.parse(upload.imageUri)
//                                    val destination = upload.placeName
//                                    val startDate = outputDateFormat.format(upload.startDate)
//                                    val endDate = outputDateFormat.format(upload.endDate)
//                                    val duration = "$startDate - $endDate"
//                                    val description = upload.description
//
//                                    viewState.boundLastTrip(name, imageUri, destination, duration, description)
//                                }
//                            } else {
//                                viewState.setLastTripVisibility(View.GONE)
//                            }
//                        } else {
//                            viewState.setLastTripVisibility(View.GONE)
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        viewState.showToast(error.message)
//                    }
//
//                }
//        )
    }

    fun initClosestTrip() {

        GlobalScope.launch {
            launch(Dispatchers.IO) {
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
    }

//        val databaseReference = database.getReference("users/${signInAccount.uid}")
//
//        databaseReference.orderByChild("startDate").addValueEventListener(
//                object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if (snapshot.exists()) {
//                            val uploadList = snapshot.children
//                                    .filter { snap ->
//                                        val value = snap.child("startDate").value as Long?
//                                        return@filter if (value != null) {
//                                            value >= System.currentTimeMillis()
//                                        } else {
//                                            false
//                                        }
//                                    }
//                            if (uploadList.isNotEmpty()) {
//                                viewState.setClosestTripVisibility(View.VISIBLE)
//
//                                val upload = uploadList.first().getValue(Trip::class.java)
//                                closestTripKey = uploadList.first().key.toString()
//
//                                if (upload != null) {
//                                    val name = upload.name
//                                    val imageUri = Uri.parse(upload.imageUri)
//                                    val destination = upload.placeName
//                                    val startDate = outputDateFormat.format(upload.startDate)
//                                    val endDate = outputDateFormat.format(upload.endDate)
//                                    val duration = "$startDate - $endDate"
//                                    val description = upload.description
//
//                                    viewState.boundClosestTrip(name, imageUri, destination, duration, description)
//                                }
//                            } else {
//                                viewState.setClosestTripVisibility(View.GONE)
//                            }
//                        } else {
//                            viewState.setClosestTripVisibility(View.GONE)
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        viewState.showToast(error.message)
//                    }
//
//                }
//        )
//}

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

        GlobalScope.launch {
            launch(Dispatchers.IO) {
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

//        val databaseReference = database.getReference("users/${signInAccount.uid}")
//
//        databaseReference.orderByChild("startDate").addValueEventListener(
//                object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if (snapshot.exists()) {
//                            val uploadList = snapshot.children
//                                    .filter { child ->
//                                        val startDate = child.child("startDate").getValue(Long::class.java)
//                                        val endDate = child.child("endDate").getValue(Long::class.java)
//                                        if (startDate != null && endDate != null) {
//                                            val currTime = System.currentTimeMillis()
//                                            startDate < currTime && currTime < endDate
//                                        } else false
//                                    }.toList()
//                            if (uploadList.isNotEmpty()) {
//                                viewState.setCurrentTripVisibility(View.VISIBLE)
//
//                                val upload = uploadList.first().getValue(Trip::class.java)
//                                currentTripKey = uploadList.first().key.toString()
//
//                                if (upload != null) {
//                                    val name = upload.name
//                                    val imageUri = Uri.parse(upload.imageUri)
//                                    val destination = upload.placeName
//                                    val startDate = outputDateFormat.format(upload.startDate)
//                                    val endDate = outputDateFormat.format(upload.endDate)
//                                    val duration = "$startDate - $endDate"
//                                    val description = upload.description
//
//                                    viewState.boundCurrentTrip(name, imageUri, destination, duration, description)
//                                }
//                            } else {
//                                viewState.setCurrentTripVisibility(View.GONE)
//                            }
//                        } else {
//                            viewState.setCurrentTripVisibility(View.GONE)
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        viewState.showToast(error.message)
//                    }
//
//                }
//        )
    }

}