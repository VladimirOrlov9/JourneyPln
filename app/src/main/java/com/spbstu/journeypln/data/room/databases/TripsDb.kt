package com.spbstu.journeypln.data.room.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spbstu.journeypln.data.room.dao.CategoriesDao
import com.spbstu.journeypln.data.room.dao.ClothesDao
import com.spbstu.journeypln.data.room.dao.TodoTasksDao
import com.spbstu.journeypln.data.room.dao.TripsDao
import com.spbstu.journeypln.data.room.entities.Category
import com.spbstu.journeypln.data.room.entities.Cloth
import com.spbstu.journeypln.data.room.entities.TodoTask
import com.spbstu.journeypln.data.room.entities.Trip

@Database(
    entities = [Trip::class, Cloth::class, TodoTask::class, Category::class],
    version = 7
)
abstract class TripsDb: RoomDatabase() {

    abstract fun todoDao(): TodoTasksDao
    abstract fun clothesDao(): ClothesDao
    abstract fun categoriesDao(): CategoriesDao
    abstract fun tripsDao(): TripsDao
}