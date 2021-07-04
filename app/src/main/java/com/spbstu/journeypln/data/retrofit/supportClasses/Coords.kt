package com.spbstu.journeypln.data.retrofit.supportClasses

class Coords (
        private val lat: Double,
        private val lng: Double
        ) {

    override fun toString(): String {
        return "$lat,$lng"
    }
}