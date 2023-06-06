package com.pulkit.todoapp

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.pulkit.todoapp.activities.AddTaskActivity
import com.pulkit.todoapp.adapter.ToDoAdapter
import com.pulkit.todoapp.databinding.ActivityMainBinding
import com.pulkit.todoapp.model.Constants
import com.pulkit.todoapp.model.ToDoItem
import com.pulkit.todoapp.viewModel.TodoViewModel


class MainActivity : AppCompatActivity(), ToDoAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var adapter: ToDoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.btnFab.setOnClickListener {
            startActivity(
                Intent(this, AddTaskActivity::class.java).putExtra(
                    Constants.TYPE,
                    Constants.ADD
                )
            )
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.list_sorted_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_sort_today -> {
                Log.d("currentdate", "onOptionsItemSelected: "+Utils.getCurrentDate()+"%")
                todoViewModel.getItemsSortedByDay(Utils.getCurrentDate()+"%")
                todoViewModel.itemsSortedByDay.observe(this) {
                    todoViewModel.checkIfDatabaseEmpty(it)

                    adapter.setItems(it)
                }
                true
            }

            R.id.menu_sort_yesterday -> {
                todoViewModel.getItemsSortedByYesterday(Utils.getYesterdayDate())
                todoViewModel.itemsSortedByYesterday.observe(this) {
                    todoViewModel.checkIfDatabaseEmpty(it)
                    adapter.setItems(it)
                }
                true
            }

            R.id.menu_sort_custom -> {

                customDateDialog(todoViewModel, adapter)
                true
            }

            R.id.menu_sort_am -> {
                todoViewModel.getAllAMSortedItems.observe(this@MainActivity) {
                    todoViewModel.checkIfDatabaseEmpty(it)
                    adapter.setItems(it)
                }
                true
            }

            R.id.menu_sort_pm -> {
                todoViewModel.getAllPMSortedItems.observe(this@MainActivity) {
                    todoViewModel.checkIfDatabaseEmpty(it)
                    adapter.setItems(it)
                }
                true
            }

            R.id.menu_sort_az -> {
                todoViewModel.getAllATOZSortedItems.observe(this@MainActivity) {
                    todoViewModel.checkIfDatabaseEmpty(it)
                    adapter.setItems(it)
                }
                true
            }

            R.id.menu_sort_za -> {
                todoViewModel.getAllZTOASortedItems.observe(this@MainActivity) {
                    todoViewModel.checkIfDatabaseEmpty(it)
                    adapter.setItems(it)
                }
                true
            }

            R.id.menu_sort_clear -> {
                todoViewModel.allTodoItems.observe(this@MainActivity) {
                    todoViewModel.checkIfDatabaseEmpty(it)
                    adapter.setItems(it)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun customDateDialog(
        todoViewModel: TodoViewModel,
        adapter: ToDoAdapter,
    ) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (dialog.window != null) dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_date_dialog)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val tvFromDate = dialog.findViewById<TextInputEditText>(R.id.tvFromDate)
        val tvToDate = dialog.findViewById<TextInputEditText>(R.id.tvToDate)
        val btnNo = dialog.findViewById<Button>(R.id.btn_no)
        val btnYes = dialog.findViewById<Button>(R.id.btn_yes)
        tvFromDate.setOnClickListener {
            Utils.showDatePicker(
                this,
                isMin = false,
                isMax = true,
                dataSetOnSpinner = false,
                text = tvFromDate,
                editText = null
            )
        }
        tvToDate.setOnClickListener {
            Utils.showDatePicker(
                this,
                isMin = false,
                isMax = true,
                dataSetOnSpinner = false,
                text = tvToDate,
                editText = null
            )
        }
        btnNo.setOnClickListener { _: View? -> dialog.dismiss() }
        btnYes.setOnClickListener {
            if (TextUtils.isEmpty(tvFromDate.text.toString().trim()) ||
                TextUtils.isEmpty(tvToDate.text.toString().trim())
            ) {
                Toast.makeText(
                    this,
                    getString(R.string.input_field_should_not_be_empty),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                todoViewModel.getItemsByDateRange(
                    tvFromDate.text.toString().trim(),
                    tvToDate.text.toString().trim()
                )
                todoViewModel.itemsSortedByRange.observe(this) {
                    Log.d("itemsSortedByRange", "onOptionsItemSelected: " + Gson().toJson(it))
                    todoViewModel.checkIfDatabaseEmpty(it)
                    adapter.setItems(it)
                }
                dialog.dismiss()

            }

        }
        dialog.show()
    }


    override fun onItemClick(item: ToDoItem) {}

    override fun onItemEdit(item: ToDoItem) {
        startActivity(
            Intent(this, AddTaskActivity::class.java)
                .putExtra(Constants.DATA, Gson().toJson(item))
                .putExtra(Constants.TYPE, Constants.EDIT)
        )
    }

    override fun onItemDelete(item: ToDoItem) {
        deleteDialog(item)

    }

    override fun onResume() {
        todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]
        adapter = ToDoAdapter(this)
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = adapter
        todoViewModel.allTodoItems.observe(this) { todoItems ->
            todoViewModel.checkIfDatabaseEmpty(todoItems)
            adapter.setItems(todoItems)
        }

        todoViewModel.emptyDatabase.observe(this) { isEmpty ->
            // Perform actions based on the value of isEmpty
            if (isEmpty) {
                binding.rvList.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                binding.rvList.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
            }
        }
        super.onResume()

    }

    private fun deleteDialog(item: ToDoItem) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (dialog.window != null) dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.delete_dialog)
        val tvDescription: AppCompatTextView =
            dialog.findViewById(R.id.tv_description)
        val tvCancel: AppCompatTextView = dialog.findViewById(R.id.tv_cancel)
        val tvOk: AppCompatTextView = dialog.findViewById(R.id.tv_ok)
        val concatenatedString = "\"${item.title}\""
        todoViewModel.changeBoldTxtFun(
            tvDescription, getString(R.string.delete_header_start) +
                    "<b> $concatenatedString </b>" +
                    getString(R.string.delete_header_end)
        )
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        tvOk.setOnClickListener {
            todoViewModel.delete(item)
            dialog.dismiss()
        }
        dialog.show()
    }

}