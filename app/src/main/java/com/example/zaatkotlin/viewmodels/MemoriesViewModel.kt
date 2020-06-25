package com.example.zaatkotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zaatkotlin.datalisteners.FirebaseQueryLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MemoriesViewModel : ViewModel() {
    private val query: Query = Firebase.firestore.collection("Memories")
        .whereEqualTo("uid", FirebaseAuth.getInstance().uid.toString())
    private val firebaseQueryLiveData = FirebaseQueryLiveData(query)
    fun getDataLive(): LiveData<QuerySnapshot> {
        return firebaseQueryLiveData
    }

    fun updateMemory(memoryID: String, title: String, memory: String, isSharing: Boolean) {
        val map = mutableMapOf<String, Any>()
        map["title"] = title
        map["memory"] = memory
        map["sharing"] = isSharing
        Firebase.firestore.collection("Memories").document(memoryID).update(map)
    }
}