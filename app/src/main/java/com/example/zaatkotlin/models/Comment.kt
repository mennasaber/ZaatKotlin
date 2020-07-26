package com.example.zaatkotlin.models

data class Comment(
    val memoryID: String,
    val userID: String,
    val commentContent: String,
    var date: String,
    var timestamp: Long
) {
    constructor() : this("", "", "", "", 0)

    lateinit var commentID: String
}