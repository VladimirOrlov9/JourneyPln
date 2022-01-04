package com.spbstu.journeypln.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.spbstu.journeypln.data.room.entities.Trip

@Dao
interface TripsDao {

    @Insert
    fun insertTrip(trip: Trip): Long

    @Query("SELECT * FROM Trip WHERE endDate < :time ORDER BY endDate LIMIT 1")
    fun findLast(time: Long): Trip

    @Query("SELECT * FROM Trip WHERE startDate > :time ORDER BY startDate LIMIT 1")
    fun findClosest(time: Long): Trip

    @Query("SELECT * FROM Trip WHERE startDate < :time AND :time < endDate ORDER BY startDate LIMIT 1")
    fun findCurrent(time: Long): Trip
}