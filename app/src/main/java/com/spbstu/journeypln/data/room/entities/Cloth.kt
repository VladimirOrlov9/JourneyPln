package com.spbstu.journeypln.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cloth (
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "isChecked") val isChecked: Boolean,
    @ColumnInfo(name = "weight") val weight: Double,
    @ColumnInfo(name = "count") val count: Int,
    @ColumnInfo(name = "categoryId") val categoryId: Long,
    @ColumnInfo(name = "tripId") val tripId: Long
)