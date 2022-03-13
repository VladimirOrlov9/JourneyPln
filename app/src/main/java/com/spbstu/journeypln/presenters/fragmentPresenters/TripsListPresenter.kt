package com.spbstu.journeypln.presenters.fragmentPresenters

import android.content.Context
import com.spbstu.journeypln.adapters.TripsRecyclerAdapter
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.data.room.entities.Trip
import com.spbstu.journeypln.views.TripsListView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moxy.MvpPresenter

class TripsListPresenter: MvpPresenter<TripsListView>() {

    private lateinit var applicationContext: Context
    private var position: Int = 0
    private lateinit var mTripsAdapter: TripsRecyclerAdapter
    private var tripsList: List<Trip> = listOf()
    private lateinit var db: TripsDb

    fun setApplicationContext(context: Context, pos: Int, db: TripsDb) {
        this.applicationContext = context
        this.position = pos
        this.db = db
    }

    fun initList() {

        CoroutineScope(Dispatchers.IO).launch {
            val tripsDao = db.tripsDao()

            when (position) {
                0 -> {
                    tripsList = tripsDao.getClosestTrips(System.currentTimeMillis())
                }
                1 -> {
                    tripsList = tripsDao.getLastTrips(System.currentTimeMillis())
                }
            }

            launch(Dispatchers.Main) {
                mTripsAdapter =
                    TripsRecyclerAdapter(context = applicationContext, trips = tripsList,
                        onClickListener = { _, trip ->
                            viewState.openClickedTrip(trip.uid, trip.name)
                        },
                    onNoItems = { visibility ->
                        viewState.updateVisibility(visibility)
                    })
                viewState.setUpAdapter(mTripsAdapter)
            }
        }
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
        removedItem?.let { item -> mTripsAdapter.restoreItem(trip = item, position = removedItemPosition) }
    }

    fun removeElementForever() {
        if (removedItem != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val tripsDao = db.tripsDao()
                tripsDao.deleteTrip(removedItem!!.uid)
            }
        }
    }
}