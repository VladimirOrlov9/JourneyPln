package com.spbstu.journeypln.model.fragments

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.room.Room
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.spbstu.journeypln.R
import com.spbstu.journeypln.data.Keys
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.model.activities.MainActivity
import com.spbstu.journeypln.presenters.fragmentPresenters.CreationNewTripPresenter
import com.spbstu.journeypln.views.CreationNewTripView
import com.squareup.picasso.Picasso
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.io.FileNotFoundException

class CreationNewTripFragment: MvpAppCompatFragment(), CreationNewTripView {

    @InjectPresenter
    lateinit var presenter: CreationNewTripPresenter

    private lateinit var datePicker: Button
    private lateinit var dateTxt: TextView
    private lateinit var tripImage: ImageView
    private lateinit var acceptFAB: ExtendedFloatingActionButton
    private lateinit var descriptionText: EditText
    private lateinit var tripName: EditText
    private lateinit var indicatorLine: LinearProgressIndicator
    private lateinit var cameraButton: Button
    private lateinit var galleryButton: Button
    private lateinit var destinationPlaceTxt: TextView
    private lateinit var pickDestinationPlaceBtn: Button
    private lateinit var pickWeightTxt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            requireActivity().applicationContext,
            TripsDb::class.java, "database"
        )
            .fallbackToDestructiveMigration()
            .build()

        presenter.setApplicationContext(requireActivity().applicationContext, db)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_creation_new_trip, container, false)
        (activity as MainActivity).supportActionBar?.title = "Новая поездка"
        init(view)

        pickDestinationPlaceBtn.setOnClickListener {
            val fields = listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            if (!Places.isInitialized()) {
                Places.initialize(requireContext(), Keys.GOOGLE_AUTOCOMPLETE_API_KEY)
            }

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(requireActivity())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        setupDatePicker()

        acceptFAB.setOnClickListener {
            val name = tripName.text.toString()
            val description = descriptionText.text.toString()
            val destination = destinationPlaceTxt.text.toString()
            val date = dateTxt.text.toString()
            val weight = pickWeightTxt.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty() && destination.isNotEmpty()
                    && date.isNotEmpty() && weight.isNotEmpty()) {
                presenter.acceptConfigAndCreateTrip(name, description, weight.toDouble())
            } else {
                showToast("Не все поля заполнены!")
            }
        }

        cameraButton.setOnClickListener {
            presenter.openCamera()
        }

        galleryButton.setOnClickListener {
            presenter.openGallery()
        }

        return view
    }

    private fun init(view: View) {
        datePicker = view.findViewById(R.id.pick_date_btn)
        dateTxt = view.findViewById(R.id.current_date_txt)
        tripImage = view.findViewById(R.id.trip_image)
        acceptFAB = view.findViewById(R.id.accept_fab)
        descriptionText = view.findViewById(R.id.trip_description_editText)
        tripName = view.findViewById(R.id.trip_name_editText)
        indicatorLine = view.findViewById(R.id.progress_line)
        galleryButton = view.findViewById(R.id.image_gallery_button)
        cameraButton = view.findViewById(R.id.image_camera_button)
        destinationPlaceTxt = view.findViewById(R.id.destination_place_txt)
        pickDestinationPlaceBtn = view.findViewById(R.id.pick_destination_place_btn)
        pickWeightTxt = view.findViewById(R.id.trip_weight_editText)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                0 -> {
                    if (resultCode == RESULT_OK) {
                        presenter.drawImageByUri()
                    }
                }
                1 -> {
                    if (resultCode == RESULT_OK && data != null) {
                        try {
                            val selectedImage = data.data
                            if (selectedImage != null) {
                                presenter.drawImageByUri(selectedImage)
                            }
                        } catch (ex: FileNotFoundException) {
                            Toast.makeText(
                                requireContext(),
                                "You haven't picked Image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                AUTOCOMPLETE_REQUEST_CODE -> {
                    if (resultCode == RESULT_OK && data != null) {
                        data.let {
                            val place = Autocomplete.getPlaceFromIntent(data)
                            if (place.id != null && place.address != null && place.latLng != null) {
                                presenter.setDestinationInfo(place.id!!, place.address!!, place.latLng!!)
                            }                        }
                    }
                }
            }
        }
    }

    private fun setupDatePicker() {
        datePicker.setOnClickListener {

            val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Выберите интервал поездки")
                    .setSelection(
                        androidx.core.util.Pair(
                            MaterialDatePicker.thisMonthInUtcMilliseconds(),
                            MaterialDatePicker.todayInUtcMilliseconds()
                        )
                    )
                    .build()
            dateRangePicker.addOnPositiveButtonClickListener {
                val selection = dateRangePicker.selection
                if (selection != null) {
                    presenter.updateDate(selection)
                    presenter.showDate()
                }
            }
            dateRangePicker.show(requireActivity().supportFragmentManager, "tag")
        }
    }

    override fun startTakePictureIntent(intent: Intent) {
        requireActivity().startActivityForResult(intent, 0)
    }

    override fun startTakePictureFromGalleryIntent(intent: Intent) {
        requireActivity().startActivityForResult(intent, 1)
    }

    override fun setDestinationText(text: String) {
        destinationPlaceTxt.text = text
    }


    override fun hideAcceptFAB() {
        acceptFAB.hide()
    }

    override fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun getBackByNavController() {
        Navigation.findNavController(requireView()).navigateUp()
    }

    override fun updateProgressLine(progress: Int) {
        indicatorLine.setProgressCompat(progress, true)
    }

    override fun updateImage(img: Uri) {
        Picasso.with(requireContext())
                .load(img)
                .into(tripImage)
    }

    override fun setTripDate(date: String) {
        dateTxt.text = date
    }

    companion object {
        const val AUTOCOMPLETE_REQUEST_CODE = 112
    }
}