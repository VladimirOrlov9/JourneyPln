package com.spbstu.journeypln.presenters.fragmentPresenters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.core.util.Pair
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.data.room.entities.Trip
import com.spbstu.journeypln.views.EditTripView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditTripPresenter: MvpPresenter<EditTripView>() {

    private lateinit var context: Context
    private lateinit var db: TripsDb

    private var currentTrip: Trip? = null
    private var imageUri: Uri? = null
    private var fileName: String? = null

    private val outputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }


    fun setContext(context: Context, db: TripsDb) {
        this.context = context
        this.db = db
    }

    fun getInfoAboutTrip(id: Long) {

        CoroutineScope(Dispatchers.IO).launch {
            val tripsDao = db.tripsDao()
            val trip = tripsDao.findTripById(id)
            currentTrip = trip

            if (trip != null) {
                val durationFrom = outputDateFormat.format(trip.startDate)
                val durationTo = outputDateFormat.format(trip.endDate)
                val duration = "$durationFrom - $durationTo"

                launch(Dispatchers.Main) {
                    viewState.updateInfoAboutTrip(
                        image = trip.imageUri, name = trip.name, location = trip.placeName,
                        duration = duration,
                        description = trip.description,
                        weight = trip.weight
                    )
                }
            }
        }
    }

//    fun setDestinationInfo(id: String, name: String, coords: LatLng) {
//        if (currentTrip != null) {
//            currentTrip!!.placeId = id
//            currentTrip!!.placeName = name
//            currentTrip!!.placeLat = coords.latitude
//            currentTrip!!.placeLong = coords.longitude
//
//            viewState.updateDestination(currentTrip!!.placeName)
//        }
//    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            fileName = absolutePath
        }
    }

    fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    imageUri = FileProvider.getUriForFile(
                        context,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    viewState.startTakePictureIntent(takePictureIntent)
                }
            }
        }
    }

    fun openGallery() {
        Intent(Intent.ACTION_PICK).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    imageUri = FileProvider.getUriForFile(
                        context,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.type = "image/*"
                    viewState.startTakePictureFromGalleryIntent(takePictureIntent)
                }
            }
        }
    }

    fun drawImageFromGalleryByUri(uri: Uri) {
        imageUri = uri

        drawImageByUri()
    }

    fun drawImageByUri() {
        if (imageUri != null) {
            viewState.updatePhoto(imageUri.toString())
        }
    }

    fun updateDate(pair: Pair<Long, Long>) {
        if (currentTrip != null) {
            currentTrip!!.startDate = pair.first
            currentTrip!!.endDate = pair.second

            val date = "${outputDateFormat.format(pair.first)} - ${outputDateFormat.format(pair.second)}"
            viewState.updateDate(date)
        }
    }

    fun saveNewInfo(name: String, description: String, weight: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            val tripsDao = db.tripsDao()
            tripsDao.updateTrip(uid = currentTrip!!.uid, name = name, description = description,
            weight = weight, startDate = currentTrip!!.startDate, endDate = currentTrip!!.endDate,
            imageUri = imageUri.toString(), placeName = currentTrip!!.placeName)

            launch(Dispatchers.Main) {
                viewState.showToast("Successfully updated!")
                viewState.getBackByNavController()
            }
        }
    }
}