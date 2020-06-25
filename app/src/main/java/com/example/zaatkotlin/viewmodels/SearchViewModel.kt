package com.example.zaatkotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zaatkotlin.datalisteners.FirebaseQueryLiveData
import com.example.zaatkotlin.models.Follow
import com.example.zaatkotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchViewModel : ViewModel() {
    var followList = ArrayList<Boolean>()
    var usersList = ArrayList<User>()
    private val query = Firebase.firestore.collection("Users")
    private val queryFollowing = Firebase.firestore.collection("Follow")
        .whereEqualTo("followerId", FirebaseAuth.getInstance().uid!!)
    private val usersQueryLiveData = FirebaseQueryLiveData(query)
    private val followingLiveData = FirebaseQueryLiveData(queryFollowing)

    fun getUsersFromDB(): LiveData<QuerySnapshot> {
        return usersQueryLiveData
    }

    fun deleteFollow(userID: String) {
        val query = Firebase.firestore.collection("Follow")
        query.whereEqualTo("followingId", userID)
            .whereEqualTo("followerId", FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot)
                    query.document(document.id).delete()
            }
    }

    fun makeFollow(userID: String) {
        val query = Firebase.firestore.collection("Follow")
        val follow = Follow()
        follow.followerId = FirebaseAuth.getInstance().uid!!
        follow.followingId = userID
        val id = follow.followerId + follow.followingId
        query.document(id).set(follow)
    }

    fun getUserFollowing(): LiveData<QuerySnapshot> {
        return followingLiveData
    }
}