package com.spbstu.journeypln.data.retrofit

import com.spbstu.journeypln.data.retrofit.pojo.weather.WeatherMain
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather?units=metric")
    fun getWeather(@Query("lat") lat: Double, @Query("lon") lon: Double,
                   @Query("appid") appid: String): Call<WeatherMain>

    companion object {
        const val BaseUrl = "https://api.openweathermap.org/"
    }
}
