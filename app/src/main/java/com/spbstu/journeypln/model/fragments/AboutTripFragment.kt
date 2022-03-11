package com.spbstu.journeypln.model.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.spbstu.journeypln.R
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.model.activities.MainActivity
import com.spbstu.journeypln.presenters.fragmentPresenters.AboutTripPresenter
import com.spbstu.journeypln.views.AboutTripView
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import kotlin.properties.Delegates

class AboutTripFragment : MvpAppCompatFragment(), AboutTripView {

    @InjectPresenter
    lateinit var presenter: AboutTripPresenter

    private lateinit var imageImg: ImageView
    private lateinit var editTripBtn: FloatingActionButton
    private lateinit var nameTxt: TextView
    private lateinit var locationTxt: TextView
    private lateinit var durationFromTxt: TextView
    private lateinit var durationToTxt: TextView
    private lateinit var descriptionTxt: TextView

    private lateinit var clothesIndicator: LinearProgressIndicator
    private lateinit var clothesSum: TextView
    private lateinit var todoIndicator: LinearProgressIndicator
    private lateinit var todoSum: TextView

    private var tripId by Delegates.notNull<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about_trip, container, false)
        init(view)

        editTripBtn.setOnClickListener {
            presenter.editTrip()
        }

        return view
    }


    override fun onResume() {
        super.onResume()

        presenter.getInfoAboutTrip(tripId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        val id = bundle?.getLong("id")
        if (id != null) {
            this.tripId = id
        }

        val db = Room.databaseBuilder(
            requireActivity().applicationContext,
            TripsDb::class.java, "database"
        )
            .fallbackToDestructiveMigration()
            .build()

        presenter.initDb(db)
    }

    override fun onStart() {
        super.onStart()

        (activity as MainActivity).supportActionBar?.title = "Информация о поездке"
    }

    private fun init(view: View) {
        imageImg = view.findViewById(R.id.trip_image)
        editTripBtn = view.findViewById(R.id.edit_trip_btn)
        locationTxt = view.findViewById(R.id.trip_location)
        durationFromTxt = view.findViewById(R.id.trip_duration_from)
        durationToTxt = view.findViewById(R.id.trip_duration_to)
        descriptionTxt = view.findViewById(R.id.trip_description)
        nameTxt = view.findViewById(R.id.trip_name)

        clothesIndicator = view.findViewById(R.id.clothes_indicator)
        clothesSum = view.findViewById(R.id.clothes_sum)
        todoIndicator = view.findViewById(R.id.todo_indicator)
        todoSum = view.findViewById(R.id.todo_sum)
    }

    override fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun updateInfoAboutTrip(image: String, name: String, location: String, durationFrom: String,
                                     durationTo: String, description: String) {
        Glide.with(requireContext())
            .load(image)
            .into(imageImg)

        nameTxt.text = name
        locationTxt.text = location
        durationFromTxt.text = durationFrom
        durationToTxt.text = durationTo
        descriptionTxt.text = description
    }

    override fun pressEditTripButton(key: Long) {
        val bundle = Bundle()
        bundle.putLong("key", key)
        Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.editTripFragment, bundle)
    }

    override fun updateProgressChecked(checked: Int, sum: Int, weightSum: Double, weightChecked: Double) {
        if (weightChecked > weightSum) {
            clothesIndicator.setIndicatorColor(Color.RED)
        } else {
            clothesIndicator.setIndicatorColor(requireActivity().getColor(R.color.st1))
        }

        val progress = when (sum) {
            0 -> 0
            else -> {
                ((checked * 100) / sum).toInt()
            }
        }

        clothesIndicator.setProgressCompat(progress, true)
        val weightCheckedStr = String.format("%.1f", weightChecked)
        clothesSum.text = "$checked/$sum            $weightCheckedStr/$weightSum кг"
    }

    override fun updateProgressTodoChecked(checked: Int, sum: Int) {
        val progress = when (sum) {
            0 -> 0
            else -> {
                ((checked * 100) / sum).toInt()
            }
        }
        todoIndicator.setProgressCompat(progress, true)
        todoSum.text = "$checked/$sum"
    }
}