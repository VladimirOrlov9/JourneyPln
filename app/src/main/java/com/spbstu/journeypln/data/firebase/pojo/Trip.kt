package com.spbstu.journeypln.data.firebase.pojo

//import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude

class Trip (
    var name: String = "",
    var placeId: String = "",
    var placeName: String = "",
    var placeLat: Double = 0.0,
    var placeLong: Double = 0.0,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var description: String = "",
    var imageUri: String = "",
    var weight: Double = 0.0
) {
    private lateinit var mKey: String

    @Exclude
    fun setKey(key: String) {
        this.mKey = key
    }

    @Exclude
    fun getKey(): String = mKey

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "placeId" to placeId,
            "placeName" to placeName,
            "placeLat" to placeLat,
            "placeLong" to placeLong,
            "startDate" to startDate,
            "endDate" to endDate,
            "description" to description,
            "imageUri" to imageUri,
            "weight" to weight
        )
    }
}