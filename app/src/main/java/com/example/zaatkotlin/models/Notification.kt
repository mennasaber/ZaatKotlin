package com.example.zaatkotlin.models

class Notification(
    val userID: String,
    val senderID: String,
    val message: String,
    val seen: Boolean
) {
    lateinit var notificationID: String
}