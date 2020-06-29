package com.example.zaatkotlin.interfaces

/**
 * this interface notify a recyclerView.Adapter of moving and dismissal(swap) event from
 * ItemTouchHelper.Callbacks
 * */
interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
    fun onItemMoveStop(fromPosition: Int, toPosition: Int)
}