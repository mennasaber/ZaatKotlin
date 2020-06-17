package com.example.zaatkotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zaatkotlin.datalisteners.FirebaseQueryLiveData
import com.example.zaatkotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class SearchViewModel : ViewModel() {
    var usersList = ArrayList<User>()
    private val query = Firebase.firestore.collection("Users")
    private val firebaseQueryLiveData = FirebaseQueryLiveData(query)
    fun getUsersFromDB() : LiveData<QuerySnapshot>{
        return firebaseQueryLiveData
    }
}