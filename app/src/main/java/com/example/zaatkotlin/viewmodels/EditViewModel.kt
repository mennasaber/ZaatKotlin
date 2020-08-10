package com.example.zaatkotlin.viewmodels

import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class EditViewModel : ViewModel() {
    var oldImage: String = ""
    var username: String = ""
    var imageUri: Uri? = null
    fun updateData(username: String, photoURL: Uri?, userID: String, oldImage: String) {
        if (photoURL != null) {
            val mImageFolderStorageRef = FirebaseStorage.getInstance().getReference("imagesFolder")
            val mImageRef = mImageFolderStorageRef.child("image" + photoURL.lastPathSegment)
            mImageRef.putFile(photoURL).addOnSuccessListener {
                mImageRef.downloadUrl.addOnSuccessListener {
                    val map = mutableMapOf<String, Any>()
                    map["username"] = username
                    map["photoURL"] = it.toString()
                    saveChanges(map, userID)
                }
            }
            FirebaseStorage.getInstance().getReferenceFromUrl(oldImage).delete()
        } else {
            val map = mutableMapOf<String, Any>()
            map["username"] = username
            saveChanges(map, userID)
        }
    }

    private fun saveChanges(map: MutableMap<String, Any>, userID: String) {
        Firebase.firestore.collection("Users").document(userID).update(map)
    }
}