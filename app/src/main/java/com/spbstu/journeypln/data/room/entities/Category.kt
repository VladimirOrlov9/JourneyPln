package com.spbstu.journeypln.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "tripId") val tripId: Long
)
