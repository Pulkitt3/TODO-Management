package com.pulkit.todoapp.viewModel

import android.app.Application
import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pulkit.todoapp.model.ToDoItem
import com.pulkit.todoapp.roomDB.ToDoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val toDoDao = ToDoDatabase.getDatabase(application).todoDao()
    private val repository: TodoRepository = TodoRepository(toDoDao)
    val allTodoItems: LiveData<List<ToDoItem>> = repository.allTodoItems
    val getAllATOZSortedItems: LiveData<List<ToDoItem>> = repository.getAllATOZSortedItems
    val getAllAMSortedItems: LiveData<List<ToDoItem>> = repository.getAllAMSortedItems
    val getAllPMSortedItems: LiveData<List<ToDoItem>> = repository.getAllPMSortedItems
    val getAllZTOASortedItems: LiveData<List<ToDoItem>> = repository.getAllZTOASortedItems
    val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _itemsSortedByDay = MutableLiveData<List<ToDoItem>>()
    private val _itemsSortedByYesterday = MutableLiveData<List<ToDoItem>>()
    private val _itemsSortedByRange = MutableLiveData<List<ToDoItem>>()
    val itemsSortedByDay: LiveData<List<ToDoItem>> = _itemsSortedByDay
    val itemsSortedByYesterday: LiveData<List<ToDoItem>> = _itemsSortedByYesterday
    val itemsSortedByRange: LiveData<List<ToDoItem>> = _itemsSortedByRange


    fun getItemsSortedByDay(dateTime: String) = viewModelScope.launch(Dispatchers.IO) {
        _itemsSortedByDay.postValue(repository.getItemsSortedByDay(dateTime))
    }

    fun getItemsSortedByYesterday(dateTime: String) = viewModelScope.launch(Dispatchers.IO) {
        _itemsSortedByYesterday.postValue(repository.getItemsSortedByYesterday(dateTime))
    }

    fun getItemsByDateRange(fromDay: String, toDay: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _itemsSortedByRange.postValue(repository.getItemsByDateRange(fromDay, toDay))

        }

    fun checkIfDatabaseEmpty(toDoData: List<ToDoItem>) {
        emptyDatabase.value = toDoData.isEmpty()
    }


    fun changeBoldTxtFun(txt: TextView, htmlTxt: String) {
        txt.text = Html.fromHtml(htmlTxt, Html.FROM_HTML_MODE_COMPACT)
    }

    fun verifyDataFromUser(title: String, time: String, timeType: String): Boolean {
        return !(title.isEmpty() || time.isEmpty() || timeType.isEmpty())
    }

    fun insert(todoItem: ToDoItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(todoItem)
    }

    fun update(todoItem: ToDoItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(todoItem)
    }

    fun delete(todoItem: ToDoItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(todoItem)
    }
}
