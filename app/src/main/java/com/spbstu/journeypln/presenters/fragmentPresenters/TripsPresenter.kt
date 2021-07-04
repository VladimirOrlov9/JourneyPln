package com.spbstu.journeypln.presenters.fragmentPresenters

import android.net.Uri
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.spbstu.journeypln.data.firebase.pojo.Trip
import com.spbstu.journeypln.views.TripsView
import moxy.MvpPresenter
import java.text.SimpleDateFormat
import java.util.*

class TripsPresenter: MvpPresenter<TripsView>() {

    private val signInAccount = FirebaseAuth.getInstance()
    private val database = Firebase.database

    private var closestTripKey: String = ""
    private var lastTripKey: String = ""
    private var currentTripKey: String = ""

    private val outputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun initLastTrip() {
        val databaseReference = database.getReference("users/${signInAccount.uid}")

        databaseReference.orderByChild("endDate").addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val uploadList = snapshot.children
                                    .filter { snap ->
                                        val value = snap.child("endDate").value as Long?
                                        return@filter if (value != null) {
                                            value < System.currentTimeMillis()
                                        } else {
                                            false
                                        }
                                    }
                            if (uploadList.isNotEmpty()) {
                                viewState.setLastTripVisibility(View.VISIBLE)

                                val upload = uploadList.last().getValue(Trip::class.java)
                                lastTripKey = uploadList.last().key.toString()

                                if (upload != null) {
                                    val name = upload.name
                                    val imageUri = Uri.parse(upload.imageUri)
                                    val destination = upload.placeName
                                    val startDate = outputDateFormat.format(upload.startDate)
                                    val endDate = outputDateFormat.format(upload.endDate)
                                    val duration = "$startDate - $endDate"
                                    val description = upload.description

                                    viewState.boundLastTrip(name, imageUri, destination, duration, description)
                                }
                            } else {
                                viewState.setLastTripVisibility(View.GONE)
                            }
                        } else {
                            viewState.setLastTripVisibility(View.GONE)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        viewState.showToast(error.message)
                    }

                }
        )
    }

    fun initClosestTrip() {
        val databaseReference = database.getReference("users/${signInAccount.uid}")

        databaseReference.orderByChild("startDate").addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val uploadList = snapshot.children
                                    .filter { snap ->
                                        val value = snap.child("startDate").value as Long?
                                        return@filter if (value != null) {
                                            value >= System.currentTimeMillis()
                                        } else {
                                            false
                                        }
                                    }
                            if (uploadList.isNotEmpty()) {
                                viewState.setClosestTripVisibility(View.VISIBLE)

                                val upload = uploadList.first().getValue(Trip::class.java)
                                closestTripKey = uploadList.first().key.toString()

                                if (upload != null) {
                                    val name = upload.name
                                    val imageUri = Uri.parse(upload.imageUri)
                                    val destination = upload.placeName
                                    val startDate = outputDateFormat.format(upload.startDate)
                                    val endDate = outputDateFormat.format(upload.endDate)
                                    val duration = "$startDate - $endDate"
                                    val description = upload.description

                                    viewState.boundClosestTrip(name, imageUri, destination, duration, description)
                                }
                            } else {
                                viewState.setClosestTripVisibility(View.GONE)
                            }
                        } else {
                            viewState.setClosestTripVisibility(View.GONE)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        viewState.showToast(error.message)
                    }

                }
        )
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
        val databaseReference = database.getReference("users/${signInAccount.uid}")

        databaseReference.orderByChild("startDate").addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val uploadList = snapshot.children
                                    .filter { child ->
                                        val startDate = child.child("startDate").getValue(Long::class.java)
                                        val endDate = child.child("endDate").getValue(Long::class.java)
                                        if (startDate != null && endDate != null) {
                                            val currTime = System.currentTimeMillis()
                                            startDate < currTime && currTime < endDate
                                        } else false
                                    }.toList()
                            if (uploadList.isNotEmpty()) {
                                viewState.setCurrentTripVisibility(View.VISIBLE)

                                val upload = uploadList.first().getValue(Trip::class.java)
                                currentTripKey = uploadList.first().key.toString()

                                if (upload != null) {
                                    val name = upload.name
                                    val imageUri = Uri.parse(upload.imageUri)
                                    val destination = upload.placeName
                                    val startDate = outputDateFormat.format(upload.startDate)
                                    val endDate = outputDateFormat.format(upload.endDate)
                                    val duration = "$startDate - $endDate"
                                    val description = upload.description

                                    viewState.boundCurrentTrip(name, imageUri, destination, duration, description)
                                }
                            } else {
                                viewState.setCurrentTripVisibility(View.GONE)
                            }
                        } else {
                            viewState.setCurrentTripVisibility(View.GONE)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        viewState.showToast(error.message)
                    }

                }
        )
    }

}