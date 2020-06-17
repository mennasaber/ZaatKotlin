package com.example.zaatkotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.models.User

class SearchAdapter(var usersList: ArrayList<User>) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    // View holder take view of item
    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTV = itemView.findViewById<TextView>(R.id.searchItemTV)
        private val photoIV = itemView.findViewById<ImageView>(R.id.searchItemIV)

        init {
            /*itemView.setOnClickListener {
                val intent :Intent = Intent(itemView.context,EditMemoryActivity::class.java)
                intent.putExtra("title",titleTV.text)
                intent.putExtra("content",memoryTV.text)
                itemView.context.startActivity(intent)
            }*/
        }

        fun setDataOfSearchItem(username: String, photoURL: String) {
            usernameTV.text = username
            //memoryTV.text = photoURL
        }

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
        holder.setDataOfSearchItem(
            username = usersList[position].username!!, //will throw null pointer exception(NPE) if it equals null
            photoURL = usersList[position].photoURL!!
        )
    }
}