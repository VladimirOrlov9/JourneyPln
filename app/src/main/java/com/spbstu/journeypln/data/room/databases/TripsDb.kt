package com.spbstu.journeypln.data.room.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spbstu.journeypln.data.room.dao.CategoriesDao
import com.spbstu.journeypln.data.room.dao.TripsDao
import com.spbstu.journeypln.data.room.entities.Category
import com.spbstu.journeypln.data.room.entities.Cloth
import com.spbstu.journeypln.data.room.entities.TodoTask
import com.spbstu.journeypln.data.room.entities.Trip

@Database(
    entities = [Trip::class, Cloth::class, TodoTask::class, Category::class],
    version = 4
)
abstract class TripsDb: RoomDatabase() {

    abstract fun categoriesDao(): CategoriesDao
    abstract fun tripsDao(): TripsDao
}