package com.spbstu.journeypln.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Trip (
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "name") val name: String,
//    @ColumnInfo(name = "placeId") val placeId: String,
    @ColumnInfo(name = "placeName") val placeName: String,
//    @ColumnInfo(name = "placeLat") val placeLat: Double,
//    @ColumnInfo(name = "placeLong") val placeLong: Double,
    @ColumnInfo(name = "startDate") val startDate: Long?,
    @ColumnInfo(name = "endDate") val endDate: Long?,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "imageUri") val imageUri: String,
    @ColumnInfo(name = "weight") val weight: Double
)