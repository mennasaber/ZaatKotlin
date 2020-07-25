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
    var isFollow: Boolean = true
    var memoriesList = ArrayList<Memory>()
    val reactMap = hashMapOf<String, Boolean>()
    val userID = FirebaseAuth.getInstance().uid!!

    fun isFollow(userID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Follow").whereEqualTo("followingId", userID)
            .whereEqualTo("followerId", this.userID)
        return FirebaseQueryLiveData(query = query)
    }

    fun getMemories(userID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Memories")
            .whereEqualTo("uid", userID)
            .whereEqualTo("sharing", true)
        return FirebaseQueryLiveData(query)
    }

    fun deleteFollow(userID: String) {
        val query = Firebase.firestore.collection("Follow")
        query.whereEqualTo("followingId", userID).whereEqualTo("followerId", this.userID).get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    query.document(document.id).delete()
                }
            }
    }

    fun makeFollow(userID: String) {
        val query = Firebase.firestore.collection("Follow")
        val follow = Follow()
        follow.followerId = this.userID
        follow.followingId = userID
        val id = follow.followerId + follow.followingId
        query.document(id).set(follow)
    }


    fun makeReact(memoryID: String) {
        reactMap[memoryID] = true
        val dataMap = hashMapOf<String, String>()
        dataMap["userID"] = userID
        dataMap["memoryID"] = memoryID
        Firebase.firestore.collection("Reacts").document(userID + memoryID).set(dataMap)
        increaseReactsCount(memoryID)
    }

    fun deleteReact(memoryID: String) {
        reactMap[memoryID] = false
        Firebase.firestore.collection("Reacts").document(userID + memoryID).delete()
        decreaseReactsCount(memoryID)
    }

    fun getUserReact(memoryID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Reacts").whereEqualTo("userID", userID)
            .whereEqualTo("memoryID", memoryID)
        return FirebaseQueryLiveData(query)
    }

    private fun increaseReactsCount(memoryID: String) {
        Firebase.firestore.collection("Memories").document(memoryID).get().addOnSuccessListener {
            Firebase.firestore.collection("Memories").document(memoryID)
                .update("lovesCount", it["lovesCount"] as Long + 1)
        }
    }

    private fun decreaseReactsCount(memoryID: String) {
        Firebase.firestore.collection("Memories").document(memoryID).get().addOnSuccessListener {
            Firebase.firestore.collection("Memories").document(memoryID)
                .update("lovesCount", it["lovesCount"] as Long - 1)
        }
    }
}