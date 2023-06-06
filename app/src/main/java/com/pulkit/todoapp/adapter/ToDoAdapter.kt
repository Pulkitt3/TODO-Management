package com.pulkit.todoapp.adapter

import android.graphics.Color
import android.graphics.Paint
import android.icu.text.CaseMap.Title
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.pulkit.todoapp.databinding.ItemTodoBinding
import com.pulkit.todoapp.model.Constants
import com.pulkit.todoapp.model.ToDoItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


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
            isTimeBeforeCurrent(
                item.time,
                item.isCompleted,
                binding.textTitle,
                binding.textStatus
            )
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

    fun getCurrentDate(): String {
        val dateTimeFormat = SimpleDateFormat(Constants.CUSTOM_DATE_FORMAT, Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        return dateTimeFormat.format(currentDate)
    }

    fun isTimeBeforeCurrent(time: String, isCompleted: Boolean, title: TextView, status: TextView) {
        val currentTime = getCurrentDate()
        val formatter = SimpleDateFormat(Constants.CUSTOM_DATE_FORMAT, Locale.getDefault())

        val dateTime = formatter.parse(time)
        val currentDateTime = formatter.parse(currentTime)
        if (dateTime != null && currentDateTime != null) {
            if (dateTime.after(currentDateTime)) {
                title.setTextColor(Color.BLACK)
            } else if (dateTime.before(currentDateTime) && !isCompleted) {
                title.setTextColor(Color.RED)
                status.visibility = View.VISIBLE
            } else {
                title.setTextColor(Color.BLACK)
            }
        }
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
