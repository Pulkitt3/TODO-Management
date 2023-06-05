package com.pulkit.todoapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pulkit.todoapp.model.LiveDataTestUtil
import com.pulkit.todoapp.model.ToDoItem
import com.pulkit.todoapp.roomDB.ToDoDao
import com.pulkit.todoapp.roomDB.ToDoDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var database: ToDoDatabase
    private lateinit var dao: ToDoDao

    @Before
    fun setup() {
        // Create an in-memory Room database
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.todoDao()
    }

    @After
    fun tearDown() {
        // Close the database after each test
        database.close()
    }

    @Test
    fun insertTodoItem() = runBlocking {
        // Create a test ToDoItem object
        val todoItem = ToDoItem(
            title = "Test Data",
            time = "12:00",
            date = System.currentTimeMillis(),
            timeType = "PM",
            isCompleted = false
        )
        // Insert the ToDoItem using the DAO method
        dao.insert(todoItem)

        // Retrieve the LiveData containing all ToDoItems
        val allItemsLiveData = dao.getAllTodoItems()

        // Observe the LiveData using LiveDataTestUtil
        val allItems = LiveDataTestUtil.getValue(allItemsLiveData)

        // Assert that the list contains the inserted item
        if (allItems != null) {
            assert(allItems.contains(todoItem))
        }
    }
}