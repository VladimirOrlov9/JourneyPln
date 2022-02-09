package com.spbstu.journeypln.model.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.room.Room
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.spbstu.journeypln.R
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.model.activities.MainActivity
import com.spbstu.journeypln.presenters.fragmentPresenters.TripsPresenter
import com.spbstu.journeypln.views.TripsView
import com.squareup.picasso.Picasso
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.io.File


class TripsFragment : MvpAppCompatFragment(), TripsView {

    @InjectPresenter
    lateinit var presenter: TripsPresenter

    private lateinit var fabButton: ExtendedFloatingActionButton
//    private lateinit var allTripsButton: Button
    private lateinit var noClosestTripText: TextView
    private lateinit var closestTripCardVIew: MaterialCardView
    private lateinit var closestTripImage: ImageView
    private lateinit var closestTripName: TextView
    private lateinit var closestTripDestination: TextView
    private lateinit var closestTripDuration: TextView
    private lateinit var closestTripInfo: TextView

    private lateinit var noLastTripText: TextView
    private lateinit var lastTripCardVIew: CardView
    private lateinit var lastTripImage: ImageView
    private lateinit var lastTripName: TextView
    private lateinit var lastTripDestination: TextView
    private lateinit var lastTripDuration: TextView
    private lateinit var lastTripInfo: TextView

    private lateinit var noCurrentTripText: TextView
    private lateinit var currentTripCardVIew: CardView
    private lateinit var currentTripImage: ImageView
    private lateinit var currentTripName: TextView
    private lateinit var currentTripDestination: TextView
    private lateinit var currentTripDuration: TextView
    private lateinit var currentTripInfo: TextView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trips, container, false)
        init(view)

        fabButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.creationNewTripFragment)
        }

//        allTripsButton.setOnClickListener {
//            Navigation.findNavController(view).navigate(R.id.allTripsFragment)
//        }

        closestTripCardVIew.setOnClickListener {
            presenter.openClosestTrip()
        }

        lastTripCardVIew.setOnClickListener {
            presenter.openLastTrip()
        }

        currentTripCardVIew.setOnClickListener {
            presenter.openCurrentTrip()
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            requireActivity().applicationContext,
            TripsDb::class.java, "database"
        )
            .fallbackToDestructiveMigration()
            .build()

        presenter.setDB(db)
    }

    override fun onStart() {
        super.onStart()

        presenter.initClosestTrip()
        presenter.initLastTrip()
        presenter.initCurrentTrip()
    }



    override fun boundLastTrip(name: String, imageUri: String, destination: String,
                               duration: String, description: String) {
        println(imageUri)

        Picasso.with(requireContext())
                .load(imageUri)
                .into(lastTripImage)
        lastTripImage.contentDescription = name
        lastTripName.text = name
        lastTripDestination.text = destination
        lastTripDuration.text = duration
        lastTripInfo.text = description
    }

    override fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun boundClosestTrip(name: String, imageUri: Uri, destination: String,
                                  duration: String, description: String) {
        if (closestTripImage.drawable == null) {
            Picasso.with(requireContext())
                .load(imageUri)
                .into(closestTripImage)
        }
        closestTripImage.contentDescription = name
        closestTripName.text = name
        closestTripDestination.text = destination
        closestTripDuration.text = duration
        closestTripInfo.text = description
    }

    override fun boundCurrentTrip(name: String, imageUri: Uri, destination: String, duration: String, description: String) {
        Picasso.with(requireContext())
                .load(imageUri)
                .into(currentTripImage)
        currentTripImage.contentDescription = name
        currentTripName.text = name
        currentTripDestination.text = destination
        currentTripDuration.text = duration
        currentTripInfo.text = description
    }


    private fun init(view: View) {
        (activity as MainActivity).supportActionBar?.title = "Поездки"
        fabButton = view.findViewById(R.id.fab)
//        allTripsButton = view.findViewById(R.id.all_trips_button)
        noClosestTripText = view.findViewById(R.id.no_closest_trip_text)
        closestTripCardVIew = view.findViewById(R.id.closest_trip_cardView)

        closestTripImage = view.findViewById(R.id.closest_trip_image)
        closestTripImage.clipToOutline = true
        closestTripName = view.findViewById(R.id.closest_trip_name)
        closestTripDestination = view.findViewById(R.id.closest_trip_destination)
        closestTripDuration = view.findViewById(R.id.closest_trip_duration)
        closestTripInfo = view.findViewById(R.id.closest_trip_info)

        noLastTripText = view.findViewById(R.id.no_last_trip_text)
        lastTripCardVIew = view.findViewById(R.id.last_trip_cardView)
        lastTripImage = view.findViewById(R.id.last_trip_image)
        lastTripImage.clipToOutline = true
        lastTripName = view.findViewById(R.id.last_trip_name)
        lastTripDestination = view.findViewById(R.id.last_trip_destination)
        lastTripDuration = view.findViewById(R.id.last_trip_duration)
        lastTripInfo = view.findViewById(R.id.last_trip_info)

        //TODO asd
        noCurrentTripText = view.findViewById(R.id.no_current_trip_text)
        currentTripCardVIew = view.findViewById(R.id.current_trip_cardView)
        currentTripImage = view.findViewById(R.id.current_trip_image)
        currentTripImage.clipToOutline = true
        currentTripName = view.findViewById(R.id.current_trip_name)
        currentTripDestination = view.findViewById(R.id.current_trip_destination)
        currentTripDuration = view.findViewById(R.id.current_trip_duration)
        currentTripInfo = view.findViewById(R.id.current_trip_info)
    }

    override fun setLastTripVisibility(visibility: Int) {
        lastTripCardVIew.visibility = visibility
    }

    override fun setClosestTripVisibility(visibility: Int) {
        closestTripCardVIew.visibility = visibility
    }

    override fun setCurrentTripVisibility(visibility: Int) {
        currentTripCardVIew.visibility = visibility
    }

    override fun openTrip(id: Long) {
        val bundle = Bundle()
        bundle.putLong("id", id)
        Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.thisTripFragment, bundle)
        //TODO open trip long
    }

}