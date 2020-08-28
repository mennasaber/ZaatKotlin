package com.example.zaatkotlin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.activities.OtherProfileActivity
import com.example.zaatkotlin.databinding.ProfileItemBinding
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.ProfileViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ProfileAdapter(
    private val usersList: ArrayList<User>, val viewModel: ProfileViewModel
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    class ProfileViewHolder(val binding: ProfileItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        return ProfileViewHolder(
            ProfileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return usersList.count()
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.apply {
            binding.progressBar.visibility = View.VISIBLE
            Picasso.get().load(usersList[position].photoURL).into(binding.userIV,
                object : Callback {
                    override fun onSuccess() {
                        binding.progressBar.visibility = View.INVISIBLE
                    }

                    override fun onError(e: Exception?) {

                    }

                })
            binding.usernameTV.text = usersList[position].username
            binding.userIV.setOnClickListener {
                goToProfile(
                    context = holder.binding.root.context,
                    position = position
                )
            }
            binding.usernameTV.setOnClickListener {
                goToProfile(
                    context = holder.binding.root.context,
                    position = position
                )
            }
        }
    }

    private fun goToProfile(
        context: Context,
        position: Int
    ) {
        val intent = Intent(context, OtherProfileActivity::class.java)
        intent.putExtra("userID", usersList[position].userId)
        intent.putExtra("username", usersList[position].username)
        intent.putExtra("photoURL", usersList[position].photoURL)
        intent.putExtra("currentUsername", viewModel.currentUser.username)
        context.startActivity(intent)
    }
}