package com.example.zaatkotlin.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.zaatkotlin.databinding.ActivityEditBinding
import com.example.zaatkotlin.databinding.LayoutTopEditProfileToolbarBinding
import com.example.zaatkotlin.viewmodels.EditViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_top_profile.view.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

class EditActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditBinding
    lateinit var toolbarBinding: LayoutTopEditProfileToolbarBinding
    val viewModel: EditViewModel by viewModels()
    lateinit var userImage: String
    lateinit var username: String
    lateinit var userID: String
    var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        toolbarBinding = LayoutTopEditProfileToolbarBinding.bind(binding.root)
        userImage = intent.getStringExtra("userImage")!!
        username = intent.getStringExtra("username")!!
        userID = intent.getStringExtra("userID")!!
        initWidget()
        setContentView(binding.root)
    }

    private fun initWidget() {
        binding.usernameET.setText(username)
        binding.progressBar.visibility = View.VISIBLE
        binding.changeIV.visibility = View.INVISIBLE

        Picasso.get().load(userImage).into(binding.userIV,
            object : Callback {
                override fun onSuccess() {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.changeIV.visibility = View.VISIBLE
                }

                override fun onError(e: Exception?) {
                    TODO("Not yet implemented")
                }

            })
        binding.changeIV.setOnClickListener { askForPermissions() }
        toolbarBinding.back.setOnClickListener { finish() }
        toolbarBinding.save.setOnClickListener {
            viewModel.updateData(
                binding.usernameET.text.trim().toString(),
                imageUri,
                userID,
                userImage
            )
            finish()
        }
    }

    private fun askForPermissions() {
        if (!isPermissionsAllowed()) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this as Activity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this as Activity,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    0
                )
            }
        } else {
            openGalleryForImage()
        }
    }

    private fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission is granted, you can perform your operation here
                openGalleryForImage()
            }
            return
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            imageUri = data?.data
            binding.userIV.setImageURI(imageUri)
        }
    }
}