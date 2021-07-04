package com.spbstu.journeypln.model.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.spbstu.journeypln.R
import com.spbstu.journeypln.data.Keys
import com.spbstu.journeypln.data.retrofit.supportClasses.Coords
import com.spbstu.journeypln.data.retrofit.PlacesApi
import com.spbstu.journeypln.data.retrofit.pojo.places.Places
import com.spbstu.journeypln.model.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HotelsFragment : Fragment() {
    private var mMap: GoogleMap? = null
    private var mTask: Task<Location>? = null
    private var locationPermissionGranted = false
    private var client: FusedLocationProviderClient? = null
    private lateinit var mapFragment: SupportMapFragment

    private lateinit var retrofit: Retrofit
    private val retrofitClient: Retrofit
        get() {
            retrofit = Retrofit.Builder()
                    .baseUrl(PlacesApi.BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            return retrofit
        }

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        mMap?.setInfoWindowAdapter(object : InfoWindowAdapter {
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val info = LinearLayout(requireContext())
                info.orientation = LinearLayout.VERTICAL
                val title = TextView(requireContext())
                title.setTextColor(Color.BLACK)
                title.gravity = Gravity.CENTER
                title.setTypeface(null, Typeface.BOLD)
                title.text = marker.title
                val snippet = TextView(requireContext())
                snippet.setTextColor(Color.GRAY)
                snippet.text = marker.snippet
                info.addView(title)
                info.addView(snippet)
                return info
            }
        })

        updateLocationUI()
    }

    private fun showCurrentLocation() {
        mTask?.addOnSuccessListener {
            val sydney = LatLng(it.latitude, it.longitude)
            findNearbyHotels(sydney, 5000)
        }
    }

    private fun findNearbyHotels(latLng: LatLng, radius: Int) {
        val apiKey = Keys.GOOGLE_MAPS_API_KEY

        val placesAPI: PlacesApi = retrofitClient.create(PlacesApi::class.java)

        val call: Call<Places> = placesAPI.getNearbyHotels(
                location = Coords(
                        lat = latLng.latitude, lng = latLng.longitude
                ),
                radius = radius,
                key = apiKey
        )
        call.enqueue(object : Callback<Places> {
            override fun onResponse(call: Call<Places>, response: Response<Places>) {

                val body = response.body()
                if (body != null) {
                    for (place in body.results) {
                        val coordinates = LatLng(place.geometry.location!!.lat, place.geometry.location.lng)

                        mMap?.addMarker(
                                MarkerOptions()
                                        .position(coordinates)
                                        .title(place.name)
                                        .snippet("${place.vicinity}" + "\n" +
                                                "Rating: ${place.rating}")
                        )


                    }
                }
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
            }

            override fun onFailure(call: Call<Places>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }

        })

    }


    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                mMap?.isMyLocationEnabled = true
                mTask = client?.lastLocation
                showCurrentLocation()
//                mMap?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                mMap?.isMyLocationEnabled = false
//                mMap?.uiSettings?.isMyLocationButtonEnabled = false
//                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getLocationPermission() {
        locationPermissionGranted = false

        if (ContextCompat.checkSelfPermission(requireActivity().applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }

        updateLocationUI()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hotels, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        client = LocationServices.getFusedLocationProviderClient(requireActivity())

        mapFragment.getMapAsync(callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as MainActivity).supportActionBar?.title = "Отели поблизости"
    }

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 184
    }
}