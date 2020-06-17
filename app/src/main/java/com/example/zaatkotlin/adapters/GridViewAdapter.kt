package com.example.zaatkotlin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.models.Memory

class GridViewAdapter(var memoriesList: ArrayList<Memory>) : BaseAdapter() {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if (view == null) {
            val inflater =
                parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.memory_item, parent, false)
        }
        val currentMemory = getItem(position)
        val titleTV = view?.findViewById<TextView>(R.id.memoryTitle)
        val memoryTV = view?.findViewById<TextView>(R.id.memoryText)
        titleTV?.let { titleTV.text = currentMemory.title }
        memoryTV?.let { memoryTV.text = currentMemory.memory }
        return view
    }

    override fun getItem(position: Int): Memory {
        return memoriesList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return memoriesList.size
    }
}