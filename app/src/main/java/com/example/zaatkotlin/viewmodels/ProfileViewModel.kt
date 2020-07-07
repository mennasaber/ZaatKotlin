package com.example.zaatkotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zaatkotlin.datalisteners.FirebaseQueryLiveData
import com.example.zaatkotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {
    var index = 0
    var followersList = ArrayList<User>()
    var followingList = ArrayList<User>()

    private val userID = FirebaseAuth.getInstance().uid
    private val followersQuery =
        Firebase.firestore.collection("Follow").whereEqualTo("followingId", userID)
    private val followingQuery =
        Firebase.firestore.collection("Follow").whereEqualTo("followerId", userID)

    private val followersLivaData = FirebaseQueryLiveData(followersQuery)
    private val followingLivaData = FirebaseQueryLiveData(followingQuery)

    fun getUserData(userID: String): LiveData<QuerySnapshot> {
        val userQuery = Firebase.firestore.collection("Users").whereEqualTo("userId", userID)
        return FirebaseQueryLiveData(userQuery)
    }

    fun getFollowers(): LiveData<QuerySnapshot> {
        return followersLivaData
    }

    fun getFollowing(): LiveData<QuerySnapshot> {
        return followingLivaData
    }
}