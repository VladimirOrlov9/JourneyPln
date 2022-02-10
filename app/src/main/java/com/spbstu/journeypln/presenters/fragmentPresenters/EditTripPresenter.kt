package com.spbstu.journeypln.presenters.fragmentPresenters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.util.Pair
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.spbstu.journeypln.data.firebase.pojo.Trip
import com.spbstu.journeypln.views.EditTripView
import moxy.MvpPresenter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditTripPresenter: MvpPresenter<EditTripView>() {

    private val signInAccount = FirebaseAuth.getInstance()
    private val database = Firebase.database
    private val storage = FirebaseStorage.getInstance()
    private lateinit var context: Context

    private var currentTrip: Trip? = null
    private var imageUri: Uri? = null
    private var fileName: String? = null

    private val outputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }


    fun setContext(context: Context) {
        this.context = context
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
                    val duration = "$durationFrom - $durationTo"
                    val weight = trip.weight

                    viewState.updateInfoAboutTrip(
                        image = imageUri, name = trip.name, location = trip.placeName,
                        duration = duration,
                        description = trip.description,
                        weight = weight
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                viewState.showToast(error.toString())
            }

        })
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

    private fun generateFileUri(name: String): Uri {
        val directory = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "JourneyPln"
        )
        if (!directory.exists()) directory.mkdirs()

        val file = File(
            directory.path + "/" + name
        )
        Log.d("PickPicture", "fileName = $file")

        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file)
    }

    private fun generateImageAttributes(): Uri {
        fileName = "${System.currentTimeMillis()}_picture.jpg"
        return generateFileUri(fileName!!)
    }

    fun openCamera() {
        imageUri = generateImageAttributes()
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        viewState.startTakePictureIntent(takePictureIntent)
    }

    fun openGallery() {
        fileName = "${System.currentTimeMillis()}_picture.jpg"
        val pickPhotoIntent = Intent(Intent.ACTION_PICK)
        pickPhotoIntent.type = "image/*"
        viewState.startTakePictureFromGalleryIntent(pickPhotoIntent)
    }

    fun drawImageFromGalleryByUri(uri: Uri) {
        imageUri = uri

        drawImageByUri()
    }

    fun drawImageByUri() {
        if (imageUri != null) {
            viewState.updatePhoto(imageUri!!)
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
        val databaseReference = database.getReference("users/${signInAccount.uid}")
        if (currentTrip != null) {
            currentTrip!!.name = name
            currentTrip!!.description = description
            currentTrip!!.weight = weight

            if (imageUri == null) {
                viewState.hideAcceptFAB()
                databaseReference.child(currentTrip!!.getKey()).updateChildren(currentTrip!!.toMap())
                    .addOnSuccessListener {
                        viewState.showToast("Upload successful!")
                        viewState.getBackByNavController()
                    }
            }
            else {
                storage.getReferenceFromUrl(currentTrip!!.imageUri).delete()

                val storageRef = storage.getReference("users/${signInAccount.uid}").child(fileName!!)
                viewState.hideAcceptFAB()
                storageRef.putFile(imageUri!!)
                    .addOnSuccessListener {
                        viewState.showToast("Upload successful!")

                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            currentTrip!!.imageUri = uri.toString()
                            databaseReference.child(currentTrip!!.getKey()).updateChildren(currentTrip!!.toMap())

                            viewState.getBackByNavController()
                        }
                    }
                    .addOnFailureListener { ex ->
                        viewState.showToast(ex.message.toString())
                    }
                    .addOnProgressListener { snapshot ->
                        val progress = (100 * snapshot.bytesTransferred) / snapshot.totalByteCount
                        viewState.updateProgressLine(progress = progress.toInt())
                    }
            }
        }
    }
}