package com.example.zaatkotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zaatkotlin.datalisteners.FirebaseQueryLiveData
import com.example.zaatkotlin.models.User
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LovesViewModel : ViewModel() {
    val usersList = ArrayList<User>()
    lateinit var currentUser: User
    fun getUsersLoveMemory(memoryID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Reacts").whereEqualTo("memoryID", memoryID)
        return FirebaseQueryLiveData(query)
    }

    fun getUserData(userID: String): LiveData<QuerySnapshot> {
        val userQuery = Firebase.firestore.collection("Users").whereEqualTo("userId", userID)
        return FirebaseQueryLiveData(userQuery)
    }
}