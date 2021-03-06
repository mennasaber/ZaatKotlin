package com.example.zaatkotlin.adapters


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.OtherProfileActivity
import com.example.zaatkotlin.databinding.SearchItemBinding
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.SearchViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class SearchAdapter(
    var usersList: ArrayList<User>,
    private var followList: MutableMap<String, Boolean>,
    private var viewModel: SearchViewModel, val context: Context?
) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {

        holder.binding.searchItemTV.text = usersList[position].username!!
        if (followList[usersList[position].userId]!!)
            holder.binding.followB.setText(R.string.unfollow)
        else
            holder.binding.followB.setText(R.string.follow)
        holder.binding.progressBar.visibility = View.VISIBLE
        Picasso.get().load(usersList[position].photoURL!!).into(holder.binding.searchItemIV,
            object : Callback {
                override fun onSuccess() {
                    holder.binding.progressBar.visibility = View.INVISIBLE
                }

                override fun onError(e: Exception?) {

                }

            })


        holder.binding.followB.setOnClickListener {
            if (holder.binding.followB.text.toString() == context?.resources?.getString(R.string.follow)) {
                holder.binding.followB.setText(R.string.unfollow)
                viewModel.makeFollow(usersList[position].userId!!)
            } else {
                holder.binding.followB.setText(R.string.follow)
                viewModel.deleteFollow(usersList[position].userId!!)
            }
        }
        holder.binding.searchItemTV.setOnClickListener {
            goToProfile(holder.itemView.context, position)
        }
        holder.binding.searchItemIV.setOnClickListener {
            goToProfile(holder.itemView.context, position)
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