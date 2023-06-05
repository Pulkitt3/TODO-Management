package com.pulkit.todoapp.activities

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.pulkit.todoapp.R
import com.pulkit.todoapp.databinding.ActivityAddTaskBinding
import com.pulkit.todoapp.model.Constants
import com.pulkit.todoapp.model.ToDoItem
import com.pulkit.todoapp.viewModel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var type: String
    private lateinit var response: ToDoItem
    lateinit var selectedValue: String


    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]
        val items = arrayOf(Constants.PM, Constants.AM)

        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        if (intent != null) {
            if (intent.getStringExtra(Constants.TYPE).equals(Constants.ADD)) {
                binding.checkboxComplete.visibility = View.GONE
                type = Constants.ADD
                binding.spinner.setSelection(0)
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
                binding.spinner.setSelection(positionToSelect)
                binding.checkboxComplete.isChecked = response.isCompleted
                binding.tidTaskTitle.setText(response.title)
                binding.tidTaskTime.setText(response.time)
                binding.btnAdd.text = getString(R.string.update)
            }
        }

        val currentTime = Calendar.getInstance()
        val currentHour = currentTime[Calendar.HOUR_OF_DAY]
        val currentMinute = currentTime[Calendar.MINUTE]

        val timePickerDialog = TimePickerDialog(
            this@AddTaskActivity,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                val selectedTime = Calendar.getInstance()
                selectedTime[Calendar.HOUR_OF_DAY] = hourOfDay
                selectedTime[Calendar.MINUTE] = minute

                // Check if the selected time is greater than the current time
                if (selectedTime.timeInMillis > currentTime.timeInMillis) {
                    val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(selectedTime.time)

                    val parts = formattedTime.split(":")
                    val hour = parts[0]
                    val minuteAndPeriod = parts[1].split(" ")
                    val minute = minuteAndPeriod[0]
                    val period = minuteAndPeriod[1]

                    val displayedTime = "$hour:$minute"
                    binding.tidTaskTime.setText(displayedTime)
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.please_select_greater_than_current_time),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            currentHour,
            currentMinute,
            false
        )

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnAdd.setOnClickListener {
            insertDataToDb(type)
        }
        binding.tidTaskTime.setOnClickListener {
            timePickerDialog.show()

        }
        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val selectedItem = parent.getItemAtPosition(position)
                selectedValue = selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

    }


    private fun insertDataToDb(type: String) {
        val task = binding.tidTaskTitle.text.toString()
        val time = binding.tidTaskTime.text.toString()
        val timeType = selectedValue

        val validation = todoViewModel.verifyDataFromUser(task, time, timeType)
        if (validation) {
            if (type.equals(Constants.ADD, true)) {
                val newTodoItem = ToDoItem(
                    title = binding.tidTaskTitle.text.toString(),
                    time = binding.tidTaskTime.text.toString(),
                    date = System.currentTimeMillis(),
                    timeType = selectedValue,
                    isCompleted = false
                )
                todoViewModel.insert(newTodoItem)
                finish()
            } else {
                val newTodoItem = ToDoItem(
                    id = response.id,
                    title = binding.tidTaskTitle.text.toString(),
                    time = binding.tidTaskTime.text.toString(),
                    date = System.currentTimeMillis(),
                    timeType = selectedValue,
                    isCompleted = binding.checkboxComplete.isChecked
                )
                todoViewModel.update(newTodoItem)
                finish()
            }
            Toast.makeText(
                this, getString(R.string.successfully_added),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this, getString(R.string.please_fill_out_all_fields),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}