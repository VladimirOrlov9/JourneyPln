package com.spbstu.journeypln.presenters.activityPresenters

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.spbstu.journeypln.views.MainView
import moxy.MvpPresenter

class MainPresenter: MvpPresenter<MainView>() {
    private val database = Firebase.database
    private val signInAccount = FirebaseAuth.getInstance()

    fun getCurrentTrip() {
        val databaseReference = database.getReference("users/${signInAccount.uid}")

        databaseReference.orderByChild("startDate").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val list = snapshot.children.filter { child ->
                                val startDate = child.child("startDate").getValue(Long::class.java)
                                val endDate = child.child("endDate").getValue(Long::class.java)
                                if (startDate != null && endDate != null) {
                                    val currTime = System.currentTimeMillis()
                                    startDate < currTime && currTime < endDate
                                } else false
                            }.toList()

                            if (list.isNotEmpty()) {
                                val tripKey = list[0].key.toString()
                                viewState.startCurrentTripPage(tripKey)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }
        )
    }
}