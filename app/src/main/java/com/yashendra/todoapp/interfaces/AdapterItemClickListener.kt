package com.yashendra.todoapp.interfaces
interface AdapterItemClickListener {
    fun onItemClicked(position: Int)
    fun onItemLongClicked(position: Int)
    fun onItemDoneClicked(position: Int)
    fun onItemDeleteClicked(position: Int)
    fun onItemEditClicked(position: Int)
}
