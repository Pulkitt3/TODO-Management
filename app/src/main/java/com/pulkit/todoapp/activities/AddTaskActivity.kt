package com.pulkit.todoapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.pulkit.todoapp.R
import com.pulkit.todoapp.Utils
import com.pulkit.todoapp.databinding.ActivityAddTaskBinding
import com.pulkit.todoapp.model.Constants
import com.pulkit.todoapp.model.ToDoItem
import com.pulkit.todoapp.viewModel.TodoViewModel

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var type: String
    private lateinit var response: ToDoItem


    @SuppressLint("ClickableViewAccessibility")
    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]
        val items = arrayOf(Constants.AM, Constants.PM)


        if (intent != null) {
            if (intent.getStringExtra(Constants.TYPE).equals(Constants.ADD)) {
                binding.checkboxComplete.visibility = View.GONE
                type = Constants.ADD
                binding.spinner.setText(items[0])
            } else {
                binding.checkboxComplete.visibility = View.VISIBLE
                type = Constants.EDIT
                response =
                    Gson().fromJson(intent.getStringExtra(Constants.DATA), ToDoItem::class.java)
                var positionToSelect = 0
                for (i in items.indices) {
                    if (items[i].equals(response.timeType, ignoreCase = true)) {
                        positionToSelect = i
                        break
                    }
                }
                binding.spinner.setText(items[positionToSelect])
                binding.checkboxComplete.isChecked = response.isCompleted
                binding.tidTaskTitle.setText(response.title)
                val dateTime = response.time.split(" ")
                val responseTime = dateTime[0] + " " + dateTime[1] + " " + dateTime[2] + " " + dateTime[3]
                binding.tidTaskTime.setText(responseTime)
                binding.btnAdd.text = getString(R.string.update)
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnAdd.setOnClickListener {
            insertDataToDb(type)
        }
        binding.tidTaskTime.setOnClickListener {
            Utils.showDatePicker(
                this,
                isMin = true,
                isMax = false,
                true,
                binding.tidTaskTime,
                binding.spinner
            )
        }
        binding.spinner.setOnClickListener {
            Utils.spinnerDialog(this, binding.spinner)
        }
        binding.btnCancel.setOnClickListener {
            finish()
        }

    }


    private fun insertDataToDb(type: String) {
        val task = binding.tidTaskTitle.text.toString()
        val time = binding.tidTaskTime.text.toString()
        val timeType = binding.spinner.text.toString()

        val validation = todoViewModel.verifyDataFromUser(task, time, timeType)
        if (validation) {
            if (type.equals(Constants.ADD, true)) {
                val newTodoItem = ToDoItem(
                    title = binding.tidTaskTitle.text.toString(),
                    time = binding.tidTaskTime.text.toString() + " " + binding.spinner.text.toString()
                        .trim(),
                    timeType = binding.spinner.text.toString().trim(),
                    isCompleted = false
                )
                todoViewModel.insert(newTodoItem)
                Toast.makeText(this, getString(R.string.successfully_added), Toast.LENGTH_SHORT)
                    .show()

                finish()
            } else {
                val newTodoItem = ToDoItem(
                    id = response.id,
                    title = binding.tidTaskTitle.text.toString(),
                    time = binding.tidTaskTime.text.toString() + " " + binding.spinner.text.toString()
                        .trim(),
                    timeType = binding.spinner.text.toString().trim(),
                    isCompleted = binding.checkboxComplete.isChecked
                )
                todoViewModel.update(newTodoItem)
                Toast.makeText(
                    this,
                    getString(R.string.successfully_updated),
                    Toast.LENGTH_SHORT
                )
                    .show()
                finish()
            }
        } else {
            Toast.makeText(
                this, getString(R.string.please_fill_out_all_fields),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}