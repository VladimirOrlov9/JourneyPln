package com.spbstu.journeypln.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.spbstu.journeypln.data.room.entities.Cloth
import com.spbstu.journeypln.data.room.entities.Trip

@Dao
interface ClothesDao {
    @Insert
    fun insertCloth(cloth: Cloth)

    @Query("SELECT COUNT(*) FROM Cloth WHERE :id = tripId")
    fun countAbsoluteNumberOfClothes(id: Long): Int

    @Query("SELECT COUNT(*) FROM Cloth WHERE :id = tripId AND isChecked = 1")
    fun countCheckedClothes(id: Long): Int

    @Query("SELECT AVG(weight) FROM Cloth WHERE :id = tripId AND isChecked = 1")
    fun countTakenWeight(id: Long): Double

}