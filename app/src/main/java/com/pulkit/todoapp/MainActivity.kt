package com.pulkit.todoapp

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
        binding.btnFab.setOnClickListener {
            startActivity(
                Intent(this, AddTaskActivity::class.java)
                    .putExtra(Constants.TYPE, Constants.ADD)

            )
        }
        binding.ibFilter.setOnClickListener {
            sortingDialog(binding.ibFilter)
        }
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

    private fun sortingDialog(anchorView: ImageButton) {
        val mView: View =
            LayoutInflater.from(this@MainActivity).inflate(R.layout.sorting_item, null, false)
        val popUp = PopupWindow(
            mView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            false
        )
        popUp.isTouchable = true
        popUp.isFocusable = true
        popUp.isOutsideTouchable = true
        popUp.showAsDropDown(anchorView)
        val tvSortAm: AppCompatTextView = mView.findViewById(R.id.tv_sort_am)
        val tvSortPm: AppCompatTextView = mView.findViewById(R.id.tv_sort_pm)
        val tvSortAz: AppCompatTextView = mView.findViewById(R.id.tv_sort_az)
        val tvSortZa: AppCompatTextView = mView.findViewById(R.id.tv_sort_za)
        val tvSortClear: AppCompatTextView = mView.findViewById(R.id.tv_sort_clear)

        tvSortAm.setOnClickListener {
            todoViewModel.getAllAMSortedItems.observe(this@MainActivity) {
                todoViewModel.checkIfDatabaseEmpty(it)
                adapter.setItems(it)
                popUp.dismiss()
            }
        }

        tvSortPm.setOnClickListener {
            todoViewModel.getAllPMSortedItems.observe(this@MainActivity) {
                todoViewModel.checkIfDatabaseEmpty(it)
                adapter.setItems(it)
                popUp.dismiss()
            }
        }

        tvSortAz.setOnClickListener {
            todoViewModel.getAllATOZSortedItems.observe(this@MainActivity) {
                todoViewModel.checkIfDatabaseEmpty(it)
                adapter.setItems(it)
                popUp.dismiss()
            }
        }

        tvSortZa.setOnClickListener {
            todoViewModel.getAllZTOASortedItems.observe(this@MainActivity) {
                todoViewModel.checkIfDatabaseEmpty(it)
                adapter.setItems(it)
                popUp.dismiss()
            }
        }
        tvSortClear.setOnClickListener {
            todoViewModel.allTodoItems.observe(this@MainActivity) {
                todoViewModel.checkIfDatabaseEmpty(it)
                adapter.setItems(it)
                popUp.dismiss()
            }
        }

    }


}