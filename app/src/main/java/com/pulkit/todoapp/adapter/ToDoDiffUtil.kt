package com.pulkit.todoapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.pulkit.todoapp.model.ToDoItem

class ToDoDiffUtil(
    private val oldList: List<ToDoItem>,
    private val newList: List<ToDoItem>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
                && oldList[oldItemPosition].title == newList[newItemPosition].title
                && oldList[oldItemPosition].time == newList[newItemPosition].time
                && oldList[oldItemPosition].timeType == newList[newItemPosition].timeType
                && oldList[oldItemPosition].isCompleted == newList[newItemPosition].isCompleted
    }
}