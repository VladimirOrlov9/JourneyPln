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
import com.spbstu.journeypln.R
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.data.room.entities.Category
import com.spbstu.journeypln.data.room.entities.Trip
import com.spbstu.journeypln.views.CreationNewTripView
import kotlinx.coroutines.*
import moxy.MvpPresenter
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class CreationNewTripPresenter: MvpPresenter<CreationNewTripView>() {

    private lateinit var applicationContext: Context

    private var startDate: Long? = System.currentTimeMillis()
    private var endDate: Long? = System.currentTimeMillis()
    private lateinit var fileName: String
    private lateinit var imageUri: Uri
//    private lateinit var destinationId: String
    private lateinit var destinationName: String
//    private lateinit var destinationCoords: LatLng

    private val defCategoriesList: List<String> = listOf("Верхняя одежда", "Одежда", "Нижнее белье", "Документы")

    private val outputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private lateinit var db: TripsDb


    fun setApplicationContext(context: Context, db: TripsDb) {
        applicationContext = context
        this.db = db
    }

//    fun setDestinationInfo(name: String) {
//        this.destinationName = name
//
//        viewState.setDestinationText(destinationName)
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

    private fun getUriToDrawable(
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

    fun acceptConfigAndCreateTrip(name: String, description: String, weight: Double, destination: String) {
        if (!this::imageUri.isInitialized) {
            fileName = "paris.png"
            imageUri = getUriToDrawable(applicationContext, R.drawable.unnamed)
        }

//        val mDestinationId = destinationId
        //        val mDestinationCoords = destinationCoords
        val startDate = startDate
        val endDate = endDate

        try {

               GlobalScope.launch {
                   launch(Dispatchers.IO) {
                       val tripsDao = db.tripsDao()
                       val categoriesDao = db.categoriesDao()

                       val newTrip = Trip(
                           name = name,
                           startDate = startDate,
                           endDate = endDate,
                           description = description,
                           imageUri = imageUri.toString(),
                           weight = weight,
                           placeName = destination
                       )

                       val id: Long = tripsDao.insertTrip(trip = newTrip)

                       for (category in defCategoriesList) {
                           categoriesDao.insertCategory(
                               Category(
                                   name = category,
                                   tripId = id
                               )
                           )
                       }
                   }
                   launch(Dispatchers.Main) {
                       viewState.getBackByNavController()
                   }
               }
        }
        catch (ex: Exception) {
            viewState.showToast(ex.message.toString())
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