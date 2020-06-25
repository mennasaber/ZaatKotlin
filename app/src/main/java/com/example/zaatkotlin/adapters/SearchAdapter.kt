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
    var followList: ArrayList<Boolean>,
    var viewModel: SearchViewModel
) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    // View holder take view of item
    class SearchViewHolder(itemView: View, viewModel: SearchViewModel) :
        RecyclerView.ViewHolder(itemView) {
        private val usernameTV = itemView.findViewById<TextView>(R.id.searchItemTV)
        private val photoIV = itemView.findViewById<ImageView>(R.id.searchItemIV)
        private val followB = itemView.findViewById<Button>(R.id.followB)
        lateinit var userID: String

        init {
            followB.setOnClickListener {
                if (followB.text.toString() == "Follow") {
                    followB.setText(R.string.unfollow)
                    viewModel.makeFollow(userID)
                } else {
                    followB.setText(R.string.follow)
                    viewModel.deleteFollow(userID)
                }
            }
        }

        fun setDataOfSearchItem(
            username: String,
            photoURL: String,
            userID: String,
            isFollow: Boolean
        ) {
            usernameTV.text = username
            this.userID = userID
            if (isFollow)
                followB.setText(R.string.unfollow)
            else
                followB.setText(R.string.follow)
            Picasso.get().load(photoURL).into(photoIV)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false),
            viewModel
        )
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.setDataOfSearchItem(
            username = usersList[position].username!!, //will throw null pointer exception(NPE) if it equals null
            photoURL = usersList[position].photoURL!!,
            userID = usersList[position].userId!!,
            isFollow = followList[position]
        )
    }
}