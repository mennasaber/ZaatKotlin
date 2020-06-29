package com.example.zaatkotlin.interfaces

/**
 * this interface notify RecyclerView.ViewHolder of relevant callbacks from
 * ItemTouchHelper.callback
 * */
interface ItemTouchHelperViewHolder {
    fun onItemSelected()
    fun onItemClear()
}