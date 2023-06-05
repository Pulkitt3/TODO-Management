package com.pulkit.todoapp.adapter

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.pulkit.todoapp.databinding.ItemTodoBinding
import com.pulkit.todoapp.model.Constants
import com.pulkit.todoapp.model.ToDoItem
import java.util.Calendar


class ToDoAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {

    private var items: List<ToDoItem> = emptyList()

    inner class ViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(holder: ViewHolder, item: ToDoItem) {
            Log.d("holder", "bind: " + Gson().toJson(item))
            binding.item = item
            if (item.isCompleted) {
                binding.textTitle.paintFlags =
                    binding.textTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.textTitle.paintFlags =
                    binding.textTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            val timeToCompare = item.time + ":" + item.timeType
            val isBeforeCurrent = isTimeBeforeCurrent(timeToCompare)
            if (isBeforeCurrent && !item.isCompleted) {
                binding.textTitle.setTextColor(Color.RED)
                binding.textStatus.visibility = View.VISIBLE
            } else {
                binding.textTitle.setTextColor(Color.BLACK)
            }
            binding.checkboxComplete.setOnCheckedChangeListener { _, _ ->
                listener.onItemClick(item)
            }
            binding.imageDelete.setOnClickListener {
                listener.onItemDelete(item)
            }
            holder.itemView.setOnClickListener {
                listener.onItemEdit(item)
            }
            binding.executePendingBindings()

        }
    }


    fun isTimeBeforeCurrent(time: String): Boolean {
        val currentTime = Calendar.getInstance()
        val compareTime = Calendar.getInstance()

        val parts = time.split(":")
        var hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val amPm = parts[2].trim()

        // Adjust the hour based on AM/PM
        if (amPm.equals(Constants.PM, ignoreCase = true) && hour != 12) {
            hour += 12
        } else if (amPm.equals(Constants.AM, ignoreCase = true) && hour == 12) {
            hour = 0
        }

        // Set the compareTime to the desired time
        compareTime[Calendar.HOUR_OF_DAY] = hour
        compareTime[Calendar.MINUTE] = minute
        compareTime[Calendar.SECOND] = 0
        compareTime[Calendar.MILLISECOND] = 0

        // Compare the times
        return compareTime.before(currentTime)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemTodoBinding = ItemTodoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(holder, item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(item: List<ToDoItem>) {
        val toDoDiffUtil = ToDoDiffUtil(items, item)
        val toDoDiffResult = DiffUtil.calculateDiff(toDoDiffUtil)
        this.items = item
        toDoDiffResult.dispatchUpdatesTo(this)
    }


    interface OnItemClickListener {
        fun onItemClick(item: ToDoItem)
        fun onItemEdit(item: ToDoItem)
        fun onItemDelete(item: ToDoItem)
    }
}
