package com.example.zaatkotlin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zaatkotlin.databinding.ActivityEditBinding
import com.example.zaatkotlin.databinding.LayoutTopEditProfileToolbarBinding
import com.squareup.picasso.Picasso

class EditActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditBinding
    lateinit var toolbarBinding: LayoutTopEditProfileToolbarBinding
    lateinit var userImage: String
    lateinit var username: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        toolbarBinding = LayoutTopEditProfileToolbarBinding.bind(binding.root)
        userImage = intent.getStringExtra("userImage")!!
        username = intent.getStringExtra("username")!!
        initWidget()
        setContentView(binding.root)
    }

    private fun initWidget() {
        binding.usernameET.setText(username)
        Picasso.get().load(userImage).into(binding.userIV)
        toolbarBinding.back.setOnClickListener { finish() }
    }
}