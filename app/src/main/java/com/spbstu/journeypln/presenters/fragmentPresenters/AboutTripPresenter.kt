package com.spbstu.journeypln.presenters.fragmentPresenters

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.spbstu.journeypln.R
import com.spbstu.journeypln.data.Keys
import com.spbstu.journeypln.data.firebase.pojo.Trip
import com.spbstu.journeypln.data.retrofit.WeatherApi
import com.spbstu.journeypln.data.retrofit.pojo.weather.WeatherMain
import com.spbstu.journeypln.views.AboutTripView
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class AboutTripPresenter: MvpPresenter<AboutTripView>() {

    private val signInAccount = FirebaseAuth.getInstance()
    private val database = Firebase.database

    private var currentTrip: Trip? = null

    private val outputDateFormat = SimpleDateFormat("EEE, d MMMM yyyy года", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val outputTimeFormat = SimpleDateFormat("HH:mm", Locale.ROOT)

    private lateinit var retrofit: Retrofit
    private val client: Retrofit
        get() {
            retrofit = Retrofit.Builder()
                .baseUrl(WeatherApi.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit
        }


    fun getInfoAboutTripFromFirebase(id: String) {
        val databaseReference = database.getReference("users/${signInAccount.uid}/$id")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val trip = snapshot.getValue(Trip::class.java)
                if (trip != null) {
                    trip.setKey(snapshot.key.toString())
                    currentTrip = trip

                    val imageUri = Uri.parse(trip.imageUri)
                    val durationFrom = outputDateFormat.format(trip.startDate)
                    val durationTo = outputDateFormat.format(trip.endDate)
                    loadWeatherStats(trip.placeLat, trip.placeLong)
                    viewState.updateInfoAboutTrip(
                        image = imageUri, name = trip.name, location = trip.placeName,
                        durationFrom = durationFrom, durationTo = durationTo,
                        description = trip.description
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                viewState.showToast(error.toString())
            }

        })
    }

    private fun loadWeatherStats(lat: Double, lon: Double) {
        val weatherApiKey = Keys.WEATHER_API_KEY
        val weatherAPI: WeatherApi = client.create(WeatherApi::class.java)

        val call: Call<WeatherMain> = weatherAPI.getWeather(lat, lon, weatherApiKey)
        call.enqueue(object : Callback<WeatherMain> {
            override fun onResponse(
                    call: Call<WeatherMain>,
                    response: Response<WeatherMain>
            ) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val temp = responseBody.main.temp.toInt().toString()
                    val tempText = "$temp°C"
                    val minTemp = "${responseBody.main.temp_min.toInt()}°C"
                    val minTempText = "Мин.: $minTemp"
                    val maxTemp = "${responseBody.main.temp_max.toInt()}°C"
                    val maxTempText = "Макс.: $maxTemp"
                    val sunrise = outputTimeFormat.format(Date(responseBody.sys.sunrise * 1000))
                    val sunset = outputTimeFormat.format(Date(responseBody.sys.sunset * 1000))
                    val humidity = "${responseBody.main.humidity}%"

                    viewState.updateTemp(
                            tempText,
                            minTempText,
                            maxTempText,
                            sunrise,
                            sunset,
                            humidity
                    )
                }
            }

            override fun onFailure(call: Call<WeatherMain>, t: Throwable) {
                Log.d("CreationNewTripFragment", "Failed to download cities.")
            }

        })
    }

    fun editTrip() {
        if (currentTrip != null) {
            viewState.pressEditTripButton(currentTrip!!.getKey())
        } else {
            viewState.showToast("Секундочку")
        }
    }

    fun getPackingSummary(id: String) {
        val databaseReference = database.getReference("users/${signInAccount.uid}/$id/clothes")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tripList = arrayListOf<Pair<Double, Boolean>>()
                if (snapshot.exists()) {
                    for (element in snapshot.children) {
                        val tempDouble = element.child("weight").getValue(Double::class.java)
                        val tempCount = element.child("count").getValue(Int::class.java)
                        val tempBool = element.child("isChecked").getValue(Boolean::class.java)
                        if (tempDouble != null && tempBool != null && tempCount != null) {
                            val weightFromCount = tempDouble * tempCount
                            tripList.add(weightFromCount to tempBool)
                        }
                    }
                    val sumClothes = snapshot.children.count()
                    val sumChecked = tripList.filter { it.second }.count()
                    var sumWeightChecked = 0.0
                    for (elem in tripList.filter { it.second }) {
                        sumWeightChecked += elem.first
                    }

                    val correctWeight = currentTrip!!.weight


                    viewState.updateProgressChecked(
                            checked = sumChecked,
                            sum = sumClothes,
                            weightSum = correctWeight,
                            weightChecked = sumWeightChecked
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                viewState.showToast(error.toString())
            }

        })
    }

    fun getTodoSummary(id: String) {
        val databaseReference = database.getReference("users/${signInAccount.uid}/$id/tasks")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tripList = arrayListOf<Boolean>()
                if (snapshot.exists()) {
                    for (element in snapshot.children) {
                        val temp = element.child("isChecked").getValue(Boolean::class.java)
                        if (temp != null) {
                            tripList.add(element = temp)
                        }
                    }
                    val sumClothes = snapshot.children.count()
                    val sumChecked = tripList.filter { it }.count()

                    viewState.updateProgressTodoChecked(
                        checked = sumChecked,
                        sum = sumClothes
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                viewState.showToast(error.toString())
            }

        })
    }

}