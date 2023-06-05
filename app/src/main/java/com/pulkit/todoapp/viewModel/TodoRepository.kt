package com.pulkit.todoapp.viewModel

import androidx.lifecycle.LiveData
import com.pulkit.todoapp.roomDB.ToDoDao
import com.pulkit.todoapp.model.ToDoItem

class TodoRepository(private val todoDao: ToDoDao) {
    val allTodoItems: LiveData<List<ToDoItem>> = todoDao.getAllTodoItems()
    val getAllATOZSortedItems: LiveData<List<ToDoItem>> = todoDao.getAllATOZSortedItems()
    val getAllAMSortedItems: LiveData<List<ToDoItem>> = todoDao.getAllAMSortedItems()
    val getAllPMSortedItems: LiveData<List<ToDoItem>> = todoDao.getAllPMSortedItems()
    val getAllZTOASortedItems: LiveData<List<ToDoItem>> = todoDao.getAllZTOASortedItems()


    suspend fun insert(todoItem: ToDoItem) {
        todoDao.insert(todoItem)
    }

    suspend fun update(todoItem: ToDoItem) {
        todoDao.update(todoItem)
    }

    suspend fun delete(todoItem: ToDoItem) {
        todoDao.delete(todoItem)
    }


}
