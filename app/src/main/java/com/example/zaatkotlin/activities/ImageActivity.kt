package com.example.zaatkotlin.activities

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import com.example.zaatkotlin.R
import com.example.zaatkotlin.databinding.ActivityImageBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_top_profile.view.*
import java.lang.Exception
import java.net.URI

class ImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = resources.getColor(R.color.colorBlack, resources.newTheme())
        }

        val uri = Uri.parse(intent.getStringExtra("imageURI"))
        binding.progressBar.visibility = View.VISIBLE
        Picasso.get().load(uri).into(binding.photoView,
            object : Callback {
                override fun onSuccess() {
                    binding.progressBar.visibility = View.INVISIBLE
                }

                override fun onError(e: Exception?) {

                }

            })
        setContentView(binding.root)
    }
}