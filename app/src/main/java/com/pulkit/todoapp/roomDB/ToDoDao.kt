package com.pulkit.todoapp.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pulkit.todoapp.model.ToDoItem

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

    @Query("SELECT * FROM todo_items ORDER BY CASE WHEN timeType = 'PM' THEN 1 ELSE 0 END, time ASC")
    fun getAllPMSortedItems(): LiveData<List<ToDoItem>>

    @Query("SELECT * FROM todo_items ORDER BY title ASC")
    fun getAllATOZSortedItems(): LiveData<List<ToDoItem>>

    @Query("SELECT * FROM todo_items ORDER BY title DESC")
    fun getAllZTOASortedItems(): LiveData<List<ToDoItem>>
}