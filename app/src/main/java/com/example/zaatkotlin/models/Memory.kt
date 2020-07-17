package com.example.zaatkotlin.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Memory(
    var title: String,
    var memory: String,
    var uID: String,
    var date: String,
    var isSharing: Boolean = false
) : Parcelable {
    lateinit var memoryID: String
    var timestamp: Long = 0
    var lovesCount = 0L
}