package com.example.zaatkotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zaatkotlin.datalisteners.FirebaseQueryLiveData
import com.example.zaatkotlin.models.Follow
import com.example.zaatkotlin.models.Memory
import com.example.zaatkotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OtherProfileViewModel : ViewModel() {
    var isFollow: String = "Follow"
    var followingList = ArrayList<User>()
    var memoriesList = ArrayList<Memory>()

    fun getMemories(userID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Memories")
            .whereEqualTo("uid", userID)
            .whereEqualTo("sharing", true)
        return FirebaseQueryLiveData(query)
    }

    fun deleteFollow(userID: String) {
        val query = Firebase.firestore.collection("Follow")
        query.whereEqualTo("followingId", userID)
            .whereEqualTo("followerId", FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    query.document(document.id).delete()
                }
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
}