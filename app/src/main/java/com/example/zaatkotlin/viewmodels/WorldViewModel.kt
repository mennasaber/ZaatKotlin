package com.example.zaatkotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zaatkotlin.datalisteners.FirebaseQueryLiveData
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WorldViewModel : ViewModel() {
    var followingList = ArrayList<User>()
    var memoriesList = ArrayList<Memory>()

    private val queryFollowing = Firebase.firestore.collection("Follow")
        .whereEqualTo("followerId", FirebaseAuth.getInstance().uid!!)
    private val followingLiveData = FirebaseQueryLiveData(queryFollowing)

    fun getMemories(userID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Memories")
            .whereEqualTo("uid", userID)
            .whereEqualTo("sharing", true)
        return FirebaseQueryLiveData(query)
    }

    fun getUserFollowing(): LiveData<QuerySnapshot> {
        return followingLiveData
    }

    fun getUserData(userID: String): LiveData<QuerySnapshot> {
        val userQuery = Firebase.firestore.collection("Users").whereEqualTo("userId", userID)
        return FirebaseQueryLiveData(userQuery)
    }

}