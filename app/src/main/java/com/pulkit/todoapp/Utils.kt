package com.pulkit.todoapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.textfield.TextInputEditText
import com.pulkit.todoapp.model.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Utils {

    companion object {
        private val selectedDateTime: Calendar = Calendar.getInstance()
        private val currentDateTime: Calendar = Calendar.getInstance()
        fun showDatePicker(
            context: Context,
            isMin: Boolean,
            isMax: Boolean,
            dataSetOnSpinner: Boolean,
            text: TextInputEditText,
            editText: AppCompatEditText?,
        ) {
            val datePicker = DatePickerDialog(
                context,
                { _, year, monthOfYear, dayOfMonth ->
                    selectedDateTime[year, monthOfYear] = dayOfMonth
                    showTimePicker(context, dataSetOnSpinner, text, editText)
                },
                currentDateTime[Calendar.YEAR],
                currentDateTime[Calendar.MONTH],
                currentDateTime[Calendar.DAY_OF_MONTH]
            )

            if (isMin && isMax) {
                datePicker.datePicker.minDate = currentDateTime.timeInMillis
                datePicker.datePicker.maxDate = currentDateTime.timeInMillis
            } else if (isMin) {
                datePicker.datePicker.minDate = currentDateTime.timeInMillis

            } else {
                datePicker.datePicker.maxDate = currentDateTime.timeInMillis
            }
            datePicker.show()
        }

        private fun showTimePicker(
            context: Context,
            dataSetOnSpinner: Boolean,
            text: TextInputEditText,
            editText: AppCompatEditText?,
        ) {
            val timePicker = TimePickerDialog(context, { _, hourOfDay, minute ->
                    selectedDateTime[Calendar.HOUR_OF_DAY] = hourOfDay
                    selectedDateTime[Calendar.MINUTE] = minute
                    handleSelectedDateTime(selectedDateTime, dataSetOnSpinner, text, editText)
                },
                currentDateTime[Calendar.HOUR_OF_DAY],
                currentDateTime[Calendar.MINUTE],
                false
            )

            // Set the minimum time to the current time
            timePicker.show()
        }

        private fun handleSelectedDateTime(
            dateTime: Calendar,
            dataSetOnSpinner: Boolean,
            text: TextInputEditText,
            editText: AppCompatEditText?,
        ) {
            val selectedDate = dateTime.time
            val formattedTime =
                SimpleDateFormat(Constants.CUSTOM_DATE_FORMAT, Locale.getDefault()).format(
                    selectedDate
                )
            val parts = formattedTime.split(" ")
            val date = parts[0] + " " + parts[1] + " " + parts[2]
            val hourSplitter = parts[3].split(":")
            val hour: String
            val minutePeriod = parts[4].uppercase()
            hour = if (minutePeriod.equals(Constants.AM, ignoreCase = true)) {
                if (hourSplitter[0] == "12"){
                    "00"
                }else{
                    hourSplitter[0]
                }

            } else {
                hourSplitter[0]

            }
            val minute = hourSplitter[1]

            val displayedTime = "$date $hour:$minute"
            text.setText(displayedTime)
            if (dataSetOnSpinner) {
                editText?.setText(minutePeriod)
            }


        }

        fun spinnerDialog(context: Context, editText: AppCompatEditText) {
            val mView: View =
                LayoutInflater.from(context).inflate(R.layout.custom_menu_item, null, false)
            val popUp = PopupWindow(
                mView,
                editText.width,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                false
            )
            popUp.isTouchable = true
            popUp.isFocusable = true
            popUp.isOutsideTouchable = true
            popUp.showAsDropDown(editText)
            val tvSortAm: AppCompatTextView = mView.findViewById(R.id.tv_am)
            val tvSortPm: AppCompatTextView = mView.findViewById(R.id.tv_pm)

            tvSortAm.setOnClickListener {
                editText.setText(tvSortAm.text.toString())
                popUp.dismiss()

            }
            tvSortPm.setOnClickListener {
                editText.setText(tvSortPm.text.toString())
                popUp.dismiss()

            }


        }

        fun getCurrentDate(): String {
            val dateTimeFormat = SimpleDateFormat(Constants.CUSTOM_DATE_FORMAT, Locale.getDefault())
            val currentDate = Calendar.getInstance().time
            return dateTimeFormat.format(currentDate)
        }

        fun getYesterdayDate(): String {
            val dateTimeFormat = SimpleDateFormat(Constants.CUSTOM_DATE_FORMAT, Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayDate = calendar.time
            return dateTimeFormat.format(yesterdayDate)
        }



    }
}