package com.example.zaatkotlin.models


class Memory(
    var title: String,
    var memory: String,
    var uID: String,
    var date: String,
    var isSharing: Boolean = false
) {
    lateinit var memoryID: String
    var timestamp: Long = 0
    var lovesCount = 0L
}