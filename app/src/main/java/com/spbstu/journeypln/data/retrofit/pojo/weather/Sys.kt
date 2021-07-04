package com.spbstu.journeypln.data.retrofit.pojo.weather

data class Sys(
    val country: String,
    val id: Int,
    val sunrise: Long,
    val sunset: Long,
    val type: Int
)