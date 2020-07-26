package com.example.zaatkotlin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.activities.OtherProfileActivity
import com.example.zaatkotlin.activities.ProfileActivity
import com.example.zaatkotlin.databinding.ProfileItemBinding
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.LovesViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class LovesAdapter(
    private val usersList: ArrayList<User>, val viewModel: LovesViewModel
) : RecyclerView.Adapter<LovesAdapter.ProfileViewHolder>() {

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
        val user = usersList[position]
        if (user.userId != FirebaseAuth.getInstance().uid) {
            val intent = Intent(context, OtherProfileActivity::class.java)
            intent.putExtra("userID", user.userId)
            intent.putExtra("username", user.username)
            intent.putExtra("photoURL", user.photoURL)
            intent.putExtra("currentUsername", viewModel.currentUser.username)
            context.startActivity(intent)
        } else {
            context.startActivity(Intent(context, ProfileActivity::class.java))
        }
    }
}