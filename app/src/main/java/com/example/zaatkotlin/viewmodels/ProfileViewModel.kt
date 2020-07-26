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

class ProfileViewModel : ViewModel() {
    var followersList = ArrayList<User>()
    var followingList = ArrayList<User>()
    var memoriesList = ArrayList<Memory>()
    val reactMap = hashMapOf<String, Boolean>()
    lateinit var currentUser: User

    lateinit var user: User
    private val userID = FirebaseAuth.getInstance().uid.toString()
    private val followersQuery =
        Firebase.firestore.collection("Follow").whereEqualTo("followingId", userID)
    private val followingQuery =
        Firebase.firestore.collection("Follow").whereEqualTo("followerId", userID)
    private val memoriesQuery =
        Firebase.firestore.collection("Memories")
            .whereEqualTo("uid", userID)

    private val followersLivaData = FirebaseQueryLiveData(followersQuery)
    private val followingLivaData = FirebaseQueryLiveData(followingQuery)
    private val memoriesLivaData = FirebaseQueryLiveData(memoriesQuery)

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

    fun getMemories(): LiveData<QuerySnapshot> {
        return memoriesLivaData
    }

    fun makeReact(memoryID: String) {
        val dataMap = hashMapOf<String, String>()
        dataMap["userID"] = userID
        dataMap["memoryID"] = memoryID
        Firebase.firestore.collection("Reacts").document(userID + memoryID).set(dataMap)
            .addOnSuccessListener {
                increaseReactsCount(memoryID)
            }
    }

    fun deleteReact(memoryID: String) {
        Firebase.firestore.collection("Reacts").document(userID + memoryID).delete()
            .addOnSuccessListener {
                decreaseReactsCount(memoryID)
            }
    }

    fun getUserReact(memoryID: String): LiveData<QuerySnapshot> {
        val query = Firebase.firestore.collection("Reacts").whereEqualTo("userID", userID)
            .whereEqualTo("memoryID", memoryID)
        return FirebaseQueryLiveData(query)
    }

    private fun increaseReactsCount(memoryID: String) {
        Firebase.firestore.collection("Reacts").whereEqualTo("memoryID", memoryID).get()
            .addOnSuccessListener {
                Firebase.firestore.collection("Memories").document(memoryID)
                    .update("lovesCount", it.count()).addOnCompleteListener {
                        reactMap[memoryID] = true
                    }
            }
    }

    private fun decreaseReactsCount(memoryID: String) {
        Firebase.firestore.collection("Reacts").whereEqualTo("memoryID", memoryID).get()
            .addOnSuccessListener {
                Firebase.firestore.collection("Memories").document(memoryID)
                    .update("lovesCount", it.count()).addOnCompleteListener {
                        reactMap[memoryID] = false
                    }
            }
    }
}