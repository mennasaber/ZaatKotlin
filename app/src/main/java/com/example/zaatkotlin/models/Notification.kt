package com.example.zaatkotlin.models

class Notification(
    val userID: String,
    val senderID: String,
    val message: String,
    val seen: Boolean,
    val memoryID: String,
    var date: String
) {
    lateinit var notificationID: String
}