package com.pulkit.todoapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_items")
data class ToDoItem(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var title: String,
    var time: String,
    var timeType: String,
    var isCompleted: Boolean)

