package com.spbstu.journeypln.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import com.spbstu.journeypln.data.room.entities.Trip

@Dao
interface TripsDao {

    @Insert
    fun insertTrip(trip: Trip): Long
}