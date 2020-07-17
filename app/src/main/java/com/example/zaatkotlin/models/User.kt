package com.example.zaatkotlin.models

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    var email: String?,
    var photoURL: String?,
    var username: String?,
    var userId: String?
) : Parcelable {

    @IgnoredOnParcel
    var gender: Int = 0 // none
}