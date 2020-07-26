package com.example.zaatkotlin.models

class Notification(
    val userID: String,
    val senderID: String,
    val message: String,
    val seen: Boolean,
    val memoryID: String,
    var date: String,
    val type: Long
) {
    lateinit var notificationID: String
}