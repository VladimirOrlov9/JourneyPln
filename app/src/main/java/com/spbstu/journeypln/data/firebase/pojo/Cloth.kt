package com.spbstu.journeypln.data.firebase.pojo

import com.google.firebase.database.Exclude

class Cloth(
    var name: String = "",
    @field:JvmField
    var isChecked: Boolean = false,
    var weight: Double = 0.0,
    var count: Int = 0,
    var category: String = ""
) {
    private lateinit var key: String

    @Exclude
    fun setKey(key: String) {
        this.key = key
    }

    @Exclude
    fun getKey() = this.key

    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "isChecked" to isChecked,
        "weight" to weight,
        "count" to count,
        "category" to category
    )
}