package com.spbstu.journeypln.data.room.dao

import androidx.room.*
import com.spbstu.journeypln.data.room.entities.Cloth
import com.spbstu.journeypln.data.room.entities.Trip

@Dao
interface ClothesDao {
    @Insert
    fun insertCloth(cloth: Cloth)

    @Delete
    fun deleteCloth(cloth: Cloth)

    @Update
    fun updateCloth(cloth: Cloth)

    @Query("SELECT COUNT(*) FROM Cloth WHERE :id = tripId")
    fun countAbsoluteNumberOfClothes(id: Long): Int

    @Query("SELECT COUNT(*) FROM Cloth WHERE :id = tripId AND isChecked = 1")
    fun countCheckedClothes(id: Long): Int

    @Query("SELECT AVG(weight) FROM Cloth WHERE :id = tripId AND isChecked = 1")
    fun countTakenWeight(id: Long): Double

    @Query("SELECT COUNT(*) FROM Cloth WHERE :id = tripId AND name = :name AND weight = :weight")
    fun ifClothWithSuchNameExist(id: Long, name: String, weight: Double): Int

    @Query("UPDATE Cloth SET count = count + :count WHERE name = :name AND tripId = :tripId")
    fun incrementCounter(name: String, count: Int, tripId: Long)

    @Query("SELECT * FROM Cloth WHERE categoryId = :categoryId AND tripId = :tripId")
    fun getClothesByCategoryId(categoryId: Long, tripId: Long): List<Cloth>

    @Query("SELECT * FROM Cloth WHERE tripId = :tripId")
    fun getAllClothes(tripId: Long): List<Cloth>

    @Query("UPDATE Cloth SET isChecked = :check WHERE uid = :uid")
    fun updateCheckup(check: Boolean, uid: Long)

    @Query("SELECT * FROM Cloth WHERE uid = :uid")
    fun getClothById(uid: Long): Cloth

    @Query("DELETE FROM Cloth WHERE categoryId = :id")
    fun deleteByCategory(id: Long)

}