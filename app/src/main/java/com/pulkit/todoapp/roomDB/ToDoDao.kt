package com.pulkit.todoapp.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pulkit.todoapp.model.ToDoItem
import java.text.SimpleDateFormat
import java.util.Date

@Dao
interface ToDoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todoItem: ToDoItem)

    @Update
    suspend fun update(todoItem: ToDoItem)

    @Delete
    suspend fun delete(todoItem: ToDoItem)

    @Query("SELECT * FROM todo_items")
    fun getAllTodoItems(): LiveData<List<ToDoItem>>

    @Query("SELECT * FROM todo_items ORDER BY CASE WHEN timeType = 'AM' THEN 0 ELSE 1 END, time ASC")
    fun getAllAMSortedItems(): LiveData<List<ToDoItem>>

    @Query("SELECT * FROM todo_items ORDER BY CASE WHEN timeType = 'PM' THEN 0 ELSE 1 END, time ASC")
    fun getAllPMSortedItems(): LiveData<List<ToDoItem>>

    @Query("SELECT * FROM todo_items ORDER BY title COLLATE NOCASE ASC")
    fun getAllATOZSortedItems(): LiveData<List<ToDoItem>>

    @Query("SELECT * FROM todo_items ORDER BY title COLLATE NOCASE DESC")
    fun getAllZTOASortedItems(): LiveData<List<ToDoItem>>

    @Query("SELECT * from todo_items where time LIKE :dateTime")
    suspend fun getItemsSortedByDay(dateTime : String): List<ToDoItem>

    @Query("SELECT * FROM todo_items WHERE time = :date ORDER BY  time ASC")
    suspend fun getItemsSortedByYesterday(date: String): List<ToDoItem>

    @Query("SELECT * FROM todo_items WHERE time BETWEEN :startDay AND :endDay ORDER BY time ASC")
    suspend fun getItemsByDateRange(startDay: String, endDay: String): List<ToDoItem>

}