package com.spbstu.journeypln.presenters.fragmentPresenters

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.spbstu.journeypln.adapters.TripsRecyclerAdapter
import com.spbstu.journeypln.data.firebase.pojo.Trip
import com.spbstu.journeypln.views.TripsListView
import moxy.MvpPresenter
import java.text.FieldPosition

class TripsListPresenter: MvpPresenter<TripsListView>() {

    private lateinit var applicationContext: Context
    private var position: Int = 0
    private lateinit var mTripsAdapter: TripsRecyclerAdapter
    private val signInAccount = FirebaseAuth.getInstance()
    private val database = Firebase.database
    private val storage = FirebaseStorage.getInstance()
    private var tripsList: ArrayList<Trip?> = arrayListOf()

    fun setApplicationContext(context: Context, pos: Int) {
        this.applicationContext = context
        this.position = pos
    }

    fun initList() {
        val newList = arrayListOf<Trip?>()
        val databaseReference = database.getReference("users/${signInAccount.uid}")

        when (position) {
            0 -> {
                databaseReference.orderByChild("startDate").addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                newList.clear()
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
                                        for (element in uploadList) {
                                            val upload = element.getValue(Trip::class.java)
                                            upload?.setKey(element.key.toString())
                                            newList.add(upload)
                                        }

                                        tripsList = newList
                                        mTripsAdapter.setData(tripsList)
                                        mTripsAdapter.notifyDataSetChanged()
                                    }
                                } else {
                                    tripsList.clear()
                                    mTripsAdapter.setData(tripsList)
                                    mTripsAdapter.notifyDataSetChanged()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                viewState.showToast(error.message)
                            }
                        }
                )
            }
            1 -> {
                databaseReference.orderByChild("startDate").addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                newList.clear()
                                if (snapshot.exists()) {
                                    val uploadList = snapshot.children
                                            .filter { snap ->
                                                val value = snap.child("startDate").value as Long?
                                                return@filter if (value != null) {
                                                    value < System.currentTimeMillis()
                                                } else {
                                                    false
                                                }
                                            }.sortedByDescending {
                                                it.child("startDate").value as Long
                                            }
                                    if (uploadList.isNotEmpty()) {
                                        for (element in uploadList) {
                                            val upload = element.getValue(Trip::class.java)
                                            upload?.setKey(element.key.toString())
                                            newList.add(upload)
                                        }

                                        tripsList = newList
                                        mTripsAdapter.setData(tripsList)
                                        mTripsAdapter.notifyDataSetChanged()
                                    }
                                } else {
                                    tripsList.clear()
                                    mTripsAdapter.setData(tripsList)
                                    mTripsAdapter.notifyDataSetChanged()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                viewState.showToast(error.message)
                            }
                        }
                )
            }
        }
        mTripsAdapter = TripsRecyclerAdapter(context = applicationContext, trips = tripsList,
            onClickListener = { _, trip ->
                if (trip != null) {
                    viewState.openClickedTrip(trip.getKey())
                }
            })
        viewState.setUpAdapter(mTripsAdapter)
    }

    private var removedItem: Trip? = null
    private var removedItemPosition: Int = 0

    fun deleteElement(position: Int) {
        removedItemPosition = position
        removedItem = mTripsAdapter.getData()[removedItemPosition]
        mTripsAdapter.removeItem(removedItemPosition)

        viewState.showSnackBarDeletedItem("Поездка была удалена.")

    }

    fun restoreItem() {
        mTripsAdapter.restoreItem(trip = removedItem, position = removedItemPosition)
    }

    fun removeElementForever() {
        val databaseReference = database.getReference("users/${signInAccount.uid}")
        val item = removedItem
        if (item != null) {
            val imageRef = storage.getReferenceFromUrl(item.imageUri)
            imageRef.delete().addOnSuccessListener {
                databaseReference.child(item.getKey()).removeValue()
            }
        }
    }
}