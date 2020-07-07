package com.example.zaatkotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zaatkotlin.datalisteners.FirebaseQueryLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WorldViewModel : ViewModel() {
    var followingList: ArrayList<String> = ArrayList()


    private val queryFollowing = Firebase.firestore.collection("Follow")
        .whereEqualTo("followerId", FirebaseAuth.getInstance().uid!!)
    private val followingLiveData = FirebaseQueryLiveData(queryFollowing)

    lateinit var query: Query
    private lateinit var firebaseQueryLiveData: FirebaseQueryLiveData

    fun getMemories(): LiveData<QuerySnapshot> {
        followingList.add("")
        query = Firebase.firestore.collection("Memories")
            .whereIn("uid", followingList)
            .whereEqualTo("sharing", true)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        firebaseQueryLiveData = FirebaseQueryLiveData(query)
        return firebaseQueryLiveData
    }

    fun getUserFollowing(): LiveData<QuerySnapshot> {
        return followingLiveData
    }
}