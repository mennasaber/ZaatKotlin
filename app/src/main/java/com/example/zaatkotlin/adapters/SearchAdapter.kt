package com.example.zaatkotlin.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.SearchViewModel
import com.squareup.picasso.Picasso

class SearchAdapter(
    var usersList: ArrayList<User>,
    var followList: MutableMap<String, Boolean>,
    private var viewModel: SearchViewModel
) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    // View holder take view of item
    class SearchViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val usernameTV: TextView = itemView.findViewById(R.id.searchItemTV)
        val photoIV: ImageView = itemView.findViewById(R.id.searchItemIV)
        val followB: Button = itemView.findViewById(R.id.followB)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {

        holder.usernameTV.text = usersList[position].username!!
        if (followList[usersList[position].userId]!!)
            holder.followB.setText(R.string.unfollow)
        else
            holder.followB.setText(R.string.follow)
        Picasso.get().load(usersList[position].photoURL!!).into(holder.photoIV)


        holder.followB.setOnClickListener {
            if (holder.followB.text.toString() == "Follow") {
                holder.followB.setText(R.string.unfollow)
                viewModel.makeFollow(usersList[position].userId!!)
            } else {
                holder.followB.setText(R.string.follow)
                viewModel.deleteFollow(usersList[position].userId!!)
            }
        }
    }

}