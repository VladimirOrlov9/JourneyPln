package com.spbstu.journeypln.model.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.spbstu.journeypln.R
import com.spbstu.journeypln.data.Keys
import com.spbstu.journeypln.model.activities.MainActivity
import com.spbstu.journeypln.presenters.fragmentPresenters.EditTripPresenter
import com.spbstu.journeypln.views.EditTripView
import com.squareup.picasso.Picasso
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.io.FileNotFoundException

class EditTripFragment: MvpAppCompatFragment(), EditTripView {

    @InjectPresenter
    lateinit var presenter: EditTripPresenter

    private lateinit var imageView: ImageView
    private lateinit var nameTripTxt: EditText
    private lateinit var destinationTxt: TextView
    private lateinit var dateTxt: TextView
    private lateinit var descriptionTxt: EditText
    private lateinit var clearNameBtn: ImageButton
    private lateinit var clearDescriptionBtn: ImageButton
//    private lateinit var editDestinationBtn: Button
    private lateinit var cameraButton: Button
    private lateinit var galleryButton: Button
    private lateinit var datePicker: Button
    private lateinit var acceptFAB: ExtendedFloatingActionButton
    private lateinit var indicatorLine: LinearProgressIndicator
    private lateinit var weightTxt: EditText

    private lateinit var tripKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val keyStr = arguments?.getString("key")
        if (keyStr != null) {
            tripKey = keyStr
        }
        (activity as MainActivity).supportActionBar?.title = "Изменить поездку"
        presenter.setContext(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_creation_new_trip, container, false)
        init(view)

        clearNameBtn.setOnClickListener {
            nameTripTxt.setText("")
        }

        clearDescriptionBtn.setOnClickListener {
            descriptionTxt.setText("")
        }

//        editDestinationBtn.setOnClickListener {
//            val fields = listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
//            if (!Places.isInitialized()) {
//                Places.initialize(requireContext(), Keys.GOOGLE_AUTOCOMPLETE_API_KEY)
//            }
//
//            // Start the autocomplete intent.
//            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
//                .build(requireActivity())
//            startActivityForResult(intent, NEW_DESTINATION)
//        }

        cameraButton.setOnClickListener {
            presenter.openCamera()
        }

        galleryButton.setOnClickListener {
            presenter.openGallery()
        }

        acceptFAB.setOnClickListener {
            val name = nameTripTxt.text.toString()
            val description = descriptionTxt.text.toString()
            val weight = weightTxt.text.toString()
            presenter.saveNewInfo(name, description, weight.toDouble())
        }

        setupDatePicker()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.getInfoAboutTripFromFirebase(tripKey)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                NEW_IMAGE_CAMERA -> {
                    if (resultCode == Activity.RESULT_OK) {
                        presenter.drawImageByUri()
                    }
                }
                NEW_IMAGE_GALLERY -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        try {
                            val selectedImage = data.data
                            if (selectedImage != null) {
                                presenter.drawImageFromGalleryByUri(selectedImage)
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
                NEW_DESTINATION -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        data.let {
                            val place = Autocomplete.getPlaceFromIntent(data)
                            if (place.id != null && place.address != null && place.latLng != null) {
                                presenter.setDestinationInfo(
                                    place.id!!,
                                    place.address!!,
                                    place.latLng!!
                                )
                            }
                        }
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
                }
            }
            dateRangePicker.show(requireActivity().supportFragmentManager, "tag")
        }
    }

    private fun init(view: View) {
        imageView = view.findViewById(R.id.trip_image)
        nameTripTxt = view.findViewById(R.id.trip_name_editText)
        destinationTxt = view.findViewById(R.id.destination_place_txt)
        dateTxt = view.findViewById(R.id.current_date_txt)
        descriptionTxt = view.findViewById(R.id.trip_description_editText)

        clearNameBtn = view.findViewById(R.id.trip_name_clear_button)
        clearDescriptionBtn = view.findViewById(R.id.trip_description_button)
//        editDestinationBtn = view.findViewById(R.id.pick_destination_place_btn)
        cameraButton = view.findViewById(R.id.image_camera_button)
        galleryButton = view.findViewById(R.id.image_gallery_button)
        datePicker = view.findViewById(R.id.pick_date_btn)
        acceptFAB = view.findViewById(R.id.accept_fab)
        indicatorLine = view.findViewById(R.id.progress_line)

        weightTxt = view.findViewById(R.id.trip_weight_editText)
    }

    override fun updatePhoto(uri: Uri) {
        Picasso.with(requireContext())
            .load(uri)
            .into(imageView)
    }

    override fun updateDate(date: String) {
        dateTxt.text = date
    }

    override fun getBackByNavController() {
        Navigation.findNavController(requireView()).navigateUp()
    }

    override fun hideAcceptFAB() {
        acceptFAB.hide()
    }

    override fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun updateProgressLine(progress: Int) {
        indicatorLine.setProgressCompat(progress, true)
    }

    override fun updateInfoAboutTrip(image: Uri, name: String, location: String,
                                     duration: String, description: String, weight: Double) {
        updatePhoto(image)
        nameTripTxt.setText(name)
        destinationTxt.text = location
        dateTxt.text = duration
        descriptionTxt.setText(description)
        weightTxt.setText(weight.toString())
    }

    override fun updateDestination(destination: String) {
        destinationTxt.text = destination
    }

    override fun startTakePictureIntent(intent: Intent) {
        requireActivity().startActivityForResult(intent, NEW_IMAGE_CAMERA)
    }

    override fun startTakePictureFromGalleryIntent(intent: Intent) {
        requireActivity().startActivityForResult(intent, NEW_IMAGE_GALLERY)
    }

    companion object {
        const val NEW_DESTINATION = 132
        const val NEW_IMAGE_CAMERA = 142
        const val NEW_IMAGE_GALLERY = 152
    }
}