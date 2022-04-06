package com.spbstu.journeypln.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.spbstu.journeypln.data.room.entities.Category

@Dao
interface CategoriesDao {

    @Insert
    fun insertCategory(category: Category)

    @Query("SELECT name FROM Category WHERE tripId = :tripId")
    fun getCategories(tripId: Long): List<String>

    @Query("SELECT uid FROM Category WHERE name = :name AND tripId = :tripId")
    fun getIdByName(name: String, tripId: Long): Long

    @Query("SELECT name FROM Category WHERE uid = :id")
    fun getNameById(id: Long): String

    @Query("DELETE FROM Category WHERE uid = :uid")
    fun deleteByUid(uid: Long)

    @Query("SELECT COUNT(*) FROM Category WHERE name = :name")
    fun isCategoryExist(name: String): Int
}