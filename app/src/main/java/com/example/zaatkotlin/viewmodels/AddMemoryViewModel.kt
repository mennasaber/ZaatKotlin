package com.example.zaatkotlin.viewmodels

import androidx.lifecycle.ViewModel
import com.example.zaatkotlin.models.Memory
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddMemoryViewModel : ViewModel() {
    var title: String = ""
    var content: String = ""
    var isSharing: Boolean = false
    fun addMemory(memoryObject: Memory) {
        val db = Firebase.firestore
        memoryObject.memoryID = db.collection("Memories").document().id
        db.collection("Memories").document(memoryObject.memoryID).set(memoryObject)
    }
}