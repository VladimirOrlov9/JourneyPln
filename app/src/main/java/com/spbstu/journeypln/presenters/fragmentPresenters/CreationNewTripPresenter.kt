package com.spbstu.journeypln.presenters.fragmentPresenters

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.AnyRes
import androidx.core.content.FileProvider
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.spbstu.journeypln.R
import com.spbstu.journeypln.data.firebase.pojo.Trip
import com.spbstu.journeypln.views.CreationNewTripView
import moxy.MvpPresenter
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class CreationNewTripPresenter: MvpPresenter<CreationNewTripView>() {

    private lateinit var applicationContext: Context

    private val database = Firebase.database
    private val signInAccount = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var startDate: Long? = System.currentTimeMillis()
    private var endDate: Long? = System.currentTimeMillis()
    private lateinit var fileName: String
    private lateinit var imageUri: Uri
    private lateinit var destinationId: String
    private lateinit var destinationName: String
    private lateinit var destinationCoords: LatLng

    private val defCategoriesList: List<String> = listOf("Верхняя одежда", "Одежда", "Нижнее белье", "Документы")

    private val outputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }


    fun setApplicationContext(context: Context) {
        applicationContext = context
    }

    fun setDestinationInfo(id: String, name: String, coords: LatLng) {
        this.destinationId = id
        this.destinationName = name
        this.destinationCoords = coords

        viewState.setDestinationText(destinationName)
    }

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
                applicationContext,
                applicationContext.packageName + ".provider",
                file)
    }

    private fun generateImageAttributes() {
        fileName = "${System.currentTimeMillis()}_picture.jpg"
        imageUri = generateFileUri(fileName)
    }

    fun openCamera() {
        generateImageAttributes()
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        viewState.startTakePictureIntent(takePictureIntent)
    }

    fun openGallery() {
        generateImageAttributes()
        val pickPhotoIntent = Intent(Intent.ACTION_PICK)
        pickPhotoIntent.type = "image/*"
        viewState.startTakePictureFromGalleryIntent(pickPhotoIntent)
    }

    fun getUriToDrawable(
        context: Context,
        @AnyRes drawableId: Int
    ): Uri {
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + context.resources.getResourcePackageName(drawableId)
                    + '/' + context.resources.getResourceTypeName(drawableId)
                    + '/' + context.resources.getResourceEntryName(drawableId)
        )
    }

    fun acceptConfigAndCreateTrip(name: String, description: String, weight: Double) {
        if (!this::imageUri.isInitialized) {
            fileName = "paris.png"
            imageUri = getUriToDrawable(applicationContext, R.drawable.unnamed)
        }

        val databaseReference = database.getReference("users/${signInAccount.uid}")
        val mDestinationId = destinationId
        val mDestinationName = destinationName
        val mDestinationCoords = destinationCoords
        val startDate = startDate
        val endDate = endDate

        val storageRef = storage.getReference("users/${signInAccount.uid}").child(fileName)
        storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    viewState.hideAcceptFAB()
                    viewState.showToast("Upload successful!")

                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val newTrip = Trip(
                                name = name, placeId = mDestinationId, placeName = mDestinationName,
                                placeLat = mDestinationCoords.latitude, placeLong = mDestinationCoords.longitude,
                                startDate = startDate, endDate = endDate, description = description,
                                imageUri = uri.toString(), weight = weight
                        )
                        val key = databaseReference.push().key.toString()
                        databaseReference.child(key).setValue(newTrip)

                        for (category in defCategoriesList) {
                            databaseReference.child("$key/categories").push().setValue(category)
                        }

                        viewState.getBackByNavController()
                    }
                }
                .addOnFailureListener{ ex ->
                    viewState.showToast(ex.message.toString())
                }
                .addOnProgressListener { snapshot ->
                    val progress = (100 * snapshot.bytesTransferred) / snapshot.totalByteCount
                    viewState.updateProgressLine(progress = progress.toInt())
                }
    }

    fun drawImageByUri() {
        viewState.updateImage(imageUri)
    }

    fun drawImageByUri(img: Uri) {
        imageUri = img
        viewState.updateImage(imageUri)
    }

    fun showDate() {
        val date = "${outputDateFormat.format(startDate)} - ${outputDateFormat.format(endDate)}"
        viewState.setTripDate(date)
    }

    fun updateDate(date: androidx.core.util.Pair<Long, Long>) {
        startDate = date.first
        endDate = date.second
    }

}