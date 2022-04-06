package com.spbstu.journeypln.presenters.fragmentPresenters

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.AnyRes
import androidx.core.content.FileProvider
import com.spbstu.journeypln.R
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.data.room.entities.Category
import com.spbstu.journeypln.data.room.entities.Trip
import com.spbstu.journeypln.views.CreationNewTripView
import kotlinx.coroutines.*
import moxy.MvpPresenter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.util.Pair
import org.jetbrains.annotations.TestOnly
import java.io.*
import java.io.IOException

class CreationNewTripPresenter: MvpPresenter<CreationNewTripView>() {

    private lateinit var applicationContext: Context

    private var startDate: Long = System.currentTimeMillis()
    private var endDate: Long = System.currentTimeMillis()
    private lateinit var fileName: String
    lateinit var imageUri: Uri

    private val defCategoriesList: List<String> = listOf("Верхняя одежда", "Одежда", "Нижнее белье", "Документы")

    private val outputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private lateinit var db: TripsDb


    fun setApplicationContext(context: Context, db: TripsDb) {
        applicationContext = context
        this.db = db
    }

    @Throws(IOException::class)
    fun createImageFile(timeInMillis: Long): File {
        val storageDir: File = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            "JPEG_${timeInMillis}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            fileName = absolutePath
        }
    }


    fun openCamera(timeInMillis: Long) {
        viewState.startTakePictureIntent(generateIntentForCamera(timeInMillis))
    }

    private fun generateIntentForCamera(timeInMillis: Long): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(applicationContext.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile(timeInMillis)
                } catch (ex: IOException) {
                    // Error occurred while creating the File

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    imageUri = FileProvider.getUriForFile(
                        applicationContext,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                }
            }
        }
    }

    fun openGallery(timeInMillis: Long) {
        viewState.startTakePictureFromGalleryIntent(generateIntentForGallery(timeInMillis))
    }

    private fun generateIntentForGallery(timeInMillis: Long): Intent {
        return Intent(Intent.ACTION_PICK).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(applicationContext.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile(timeInMillis)
                } catch (ex: IOException) {
                    // Error occurred while creating the File

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    imageUri = FileProvider.getUriForFile(
                        applicationContext,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.type = "image/*"
                }
            }
        }
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

        try {
            CoroutineScope(Dispatchers.IO).launch {
                addNewTripToDB(name, description, weight, destination)

                launch(Dispatchers.Main) {
                    viewState.getBackByNavController()
                }
            }
        }
        catch (ex: Exception) {
            viewState.showToast(ex.message.toString())
        }
    }

    private fun addNewTripToDB(name: String, description: String, weight: Double, destination: String) {
        val startDate = startDate
        val endDate = endDate

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

    fun drawImageByUri() {
        viewState.updateImage(imageUri)
    }

    fun drawImageByUri(img: Uri) {
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(
                applicationContext.contentResolver,
                img
            )
        } else {
            val source = ImageDecoder.createSource(applicationContext.contentResolver, img)
            ImageDecoder.decodeBitmap(source)
        }

        val file = applicationContext.contentResolver.openOutputStream(imageUri)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, file)

        viewState.updateImage(imageUri)
    }

    fun showDate() {
        val date = "${outputDateFormat.format(startDate)} - ${outputDateFormat.format(endDate)}"
        viewState.setTripDate(date)
    }

    fun updateDate(date: kotlin.Pair<Long, Long>) {
        startDate = date.first
        endDate = date.second
    }

}