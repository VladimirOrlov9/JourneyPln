package com.spbstu.journeypln.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import com.spbstu.journeypln.data.room.entities.Category

@Dao
interface CategoriesDao {

    @Insert
    fun insertCategory(category: Category)
}