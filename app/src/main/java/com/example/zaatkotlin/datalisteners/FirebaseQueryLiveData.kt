package com.example.zaatkotlin.datalisteners


import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase


class FirebaseQueryLiveData(var query: Query) : LiveData<QuerySnapshot>() {
    override fun onActive() {
        super.onActive()
        query.addSnapshotListener(MetadataChanges.INCLUDE) { snapShot, _ ->
            value = snapShot
        }
    }
}