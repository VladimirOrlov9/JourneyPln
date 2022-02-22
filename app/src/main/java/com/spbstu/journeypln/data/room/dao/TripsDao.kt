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
    fun findLast(time: Long): Trip?

    @Query("SELECT * FROM Trip WHERE startDate > :time ORDER BY startDate LIMIT 1")
    fun findClosest(time: Long): Trip?

    @Query("SELECT * FROM Trip WHERE startDate < :time AND :time < endDate ORDER BY startDate LIMIT 1")
    fun findCurrent(time: Long): Trip?

    @Query("SELECT * FROM Trip WHERE startDate > :time ORDER BY startDate")
    fun getClosestTrips(time: Long): List<Trip>

    @Query("SELECT * FROM Trip WHERE endDate < :time ORDER BY endDate")
    fun getLastTrips(time: Long): List<Trip>

    @Query("DELETE FROM Trip WHERE uid = :id")
    fun deleteTrip(id: Long)

    @Query("SELECT * FROM Trip WHERE uid = :id LIMIT 1")
    fun findTripById(id: Long): Trip?

    @Query("""UPDATE Trip SET name = :name, description = :description, 
                    weight = :weight, startDate = :startDate, endDate = :endDate,
                    imageUri = :imageUri, placeName = :placeName WHERE uid = :uid""")
    fun updateTrip(uid: Long, name: String, description: String, weight: Double,
                   startDate: Long, endDate: Long, imageUri: String, placeName: String)
}