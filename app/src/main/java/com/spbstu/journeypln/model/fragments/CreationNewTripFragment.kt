package com.spbstu.journeypln.model.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.room.Room
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.spbstu.journeypln.R
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.model.activities.MainActivity
import com.spbstu.journeypln.presenters.fragmentPresenters.CreationNewTripPresenter
import com.spbstu.journeypln.views.CreationNewTripView
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.io.FileNotFoundException

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.util.toKotlinPair
import com.bumptech.glide.Glide


class CreationNewTripFragment: MvpAppCompatFragment(), CreationNewTripView {

    @InjectPresenter
    lateinit var presenter: CreationNewTripPresenter

    private lateinit var someActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var datePicker: Button
    private lateinit var dateTxt: TextView
    private lateinit var tripImage: ImageView
    private lateinit var acceptFAB: ExtendedFloatingActionButton
    private lateinit var descriptionText: EditText
    private lateinit var tripName: EditText
    private lateinit var indicatorLine: LinearProgressIndicator
    private lateinit var cameraButton: Button
    private lateinit var galleryButton: Button
    private lateinit var destinationPlaceTxt: EditText
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

        someActivityResultLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {
                let {
                    presenter.drawImageByUri()
                }
            }
        }

        galleryResultLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {

                val data: Intent? = result.data
                let {
                    try {
                        val selectedImage = data?.data
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
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_creation_new_trip, container, false)
        (activity as MainActivity).supportActionBar?.title = "Новая поездка"
        init(view)

        setupDatePicker()

        acceptFAB.setOnClickListener {
            val name = tripName.text.toString()
            val description = descriptionText.text.toString()
            val date = dateTxt.text.toString()
            val weight = pickWeightTxt.text.toString()
            val dest = destinationPlaceTxt.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty()
                    && date.isNotEmpty() && weight.isNotEmpty()) {
                presenter.acceptConfigAndCreateTrip(name, description, weight.toDouble(), dest)
            } else {
                showToast("Не все поля заполнены!")
            }
        }

        cameraButton.setOnClickListener {
            presenter.openCamera(System.currentTimeMillis())
        }

        galleryButton.setOnClickListener {
            presenter.openGallery(System.currentTimeMillis())
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
        pickWeightTxt = view.findViewById(R.id.trip_weight_editText)
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
                    presenter.updateDate(selection.toKotlinPair())
                    presenter.showDate()
                }
            }
            dateRangePicker.show(requireActivity().supportFragmentManager, "tag")
        }
    }

    override fun startTakePictureIntent(intent: Intent) {
        someActivityResultLauncher.launch(intent)
    }

    override fun startTakePictureFromGalleryIntent(intent: Intent) {
        galleryResultLauncher.launch(intent)
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
        Glide
            .with(requireContext())
            .load(img)
            .into(tripImage)
    }

    override fun setTripDate(date: String) {
        dateTxt.text = date
    }
}