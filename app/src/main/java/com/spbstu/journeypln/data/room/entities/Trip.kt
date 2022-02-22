package com.spbstu.journeypln.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Trip (
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "name") var name: String,
//    @ColumnInfo(name = "placeId") val placeId: String,
    @ColumnInfo(name = "placeName") var placeName: String,
//    @ColumnInfo(name = "placeLat") val placeLat: Double,
//    @ColumnInfo(name = "placeLong") val placeLong: Double,
    @ColumnInfo(name = "startDate") var startDate: Long,
    @ColumnInfo(name = "endDate") var endDate: Long,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "imageUri") var imageUri: String,
    @ColumnInfo(name = "weight") var weight: Double
)