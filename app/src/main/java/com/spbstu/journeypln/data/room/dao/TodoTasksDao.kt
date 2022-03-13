package com.spbstu.journeypln.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.spbstu.journeypln.data.room.entities.Cloth
import com.spbstu.journeypln.data.room.entities.TodoTask

@Dao
interface TodoTasksDao {

    @Insert
    fun insertTask(task: TodoTask)

    @Query("SELECT COUNT(*) FROM TodoTask WHERE :id = tripId")
    fun countAbsoluteNumberOfTasks(id: Long): Int

    @Query("SELECT COUNT(*) FROM TodoTask WHERE :id = tripId AND isChecked = 1")
    fun countNumberOfCheckedTasks(id: Long): Int

    @Query("SELECT * FROM TodoTask WHERE :tripId = tripId ORDER BY time")
    fun getAllTasks(tripId: Long): List<TodoTask>

    @Query("UPDATE TodoTask SET isChecked = :status WHERE uid = :taskId")
    fun updateCheckStatus(taskId: Long, status: Boolean)

    @Query("DELETE FROM TodoTask WHERE uid = :taskId")
    fun deleteTask(taskId: Long)
}