package com.example.zaatkotlin.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.activities.EditMemoryActivity
import com.example.zaatkotlin.R
import com.example.zaatkotlin.models.Memory

class RecyclerViewAdapter(var memoriesList: ArrayList<Memory>) :
    RecyclerView.Adapter<RecyclerViewAdapter.MemoryViewHolder>() {
    // View holder take view of item
    class MemoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTV: TextView = itemView.findViewById(R.id.memoryTitle)
        private val memoryTV: TextView = itemView.findViewById(R.id.memoryText)
        init {
            itemView.setOnClickListener {
                val intent :Intent = Intent(itemView.context,
                    EditMemoryActivity::class.java)
                intent.putExtra("title",titleTV.text)
                intent.putExtra("content",memoryTV.text)
                itemView.context.startActivity(intent)
            }
        }
        fun setDataOfMemory(title: String, memory: String) {
            titleTV.text = title
            memoryTV.text = memory
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        return MemoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.memory_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return memoriesList.size
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        holder.setDataOfMemory(
            title = memoriesList[position].title,
            memory = memoriesList[position].memory
        )
    }
}