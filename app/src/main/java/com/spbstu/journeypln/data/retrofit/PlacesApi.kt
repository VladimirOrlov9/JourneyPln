package com.spbstu.journeypln.data.retrofit

import com.spbstu.journeypln.data.retrofit.pojo.places.Places
import com.spbstu.journeypln.data.retrofit.supportClasses.Coords
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {

    @GET("maps/api/place/nearbysearch/json?types=lodging&sensor=true&language=ru")
    fun getNearbyHotels(@Query("location") location: Coords, @Query("radius") radius: Int,
                        @Query("key") key: String): Call<Places>

    companion object {
        const val BaseUrl = "https://maps.googleapis.com/"
    }
}
