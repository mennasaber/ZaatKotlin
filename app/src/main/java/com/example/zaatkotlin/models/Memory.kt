package com.example.zaatkotlin.models

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class Memory(
    var title: String,
    var memory: String,
    var uID: String,
    var date: String,
    var isSharing: Boolean = false
) : Parcelable {
    @IgnoredOnParcel
    lateinit var memoryID: String

    @IgnoredOnParcel
    var timestamp: Long = 0

    @IgnoredOnParcel
    var lovesCount = 0L

    @IgnoredOnParcel
    var commentsCount = 0L
}