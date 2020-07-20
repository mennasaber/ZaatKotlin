package com.example.zaatkotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.databinding.ProfileItemBinding
import com.example.zaatkotlin.models.User
import com.squareup.picasso.Picasso

class ProfileAdapter(
    private val usersList: ArrayList<User>
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
            Picasso.get().load(usersList[position].photoURL).into(binding.userIV)
            binding.usernameTV.text = usersList[position].username
        }
    }
}