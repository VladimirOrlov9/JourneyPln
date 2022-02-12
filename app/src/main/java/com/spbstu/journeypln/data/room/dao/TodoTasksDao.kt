package com.spbstu.journeypln.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.spbstu.journeypln.data.room.entities.Cloth

@Dao
interface TodoTasksDao {

    @Insert
    fun insertCloth(cloth: Cloth)

    @Query("SELECT COUNT(*) FROM Cloth WHERE :id = tripId")
    fun countAbsoluteNumberOfTasks(id: Long): Int

    @Query("SELECT COUNT(*) FROM Cloth WHERE :id = tripId AND isChecked = 1")
    fun countNumberOfCheckedTasks(id: Long): Int
}