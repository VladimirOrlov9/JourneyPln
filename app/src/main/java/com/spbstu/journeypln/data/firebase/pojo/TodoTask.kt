package com.spbstu.journeypln.data.firebase.pojo

import com.google.firebase.database.Exclude

class TodoTask (
    var name: String = "",
    @field:JvmField
    var isChecked: Boolean = false,
    var time: Long = 0
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
            "isChecked" to isChecked,
            "time" to time
        )
    }

}