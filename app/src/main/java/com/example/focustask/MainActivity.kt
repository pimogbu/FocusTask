package com.example.focustask

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ImageButton 
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class TaskData(val text: String, val isChecked: Boolean)

class MainActivity : AppCompatActivity() {

    private lateinit var taskContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonAdd = findViewById<Button>(R.id.buttonAdd)

        val imageButtonDeleteAllChecked = findViewById<ImageButton>(R.id.imageButtonDeleteAllChecked)

        taskContainer = findViewById(R.id.taskContainer)

        loadTasks()

        buttonAdd.setOnClickListener {
            addNewTask("", false)
        }

        imageButtonDeleteAllChecked.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    override fun onStop() {
        super.onStop()
        saveTasks()
    }

    private fun addNewTask(text: String, isChecked: Boolean) {
        val taskView = layoutInflater.inflate(R.layout.task_template, null)

        val checkBox = taskView.findViewById<CheckBox>(R.id.checkBox)
        val editText = taskView.findViewById<EditText>(R.id.editTextTask)
        val defaultColor = editText.currentTextColor

        editText.setText(text)
        checkBox.isChecked = isChecked
        updateTextStyle(editText, isChecked, defaultColor)

        checkBox.setOnCheckedChangeListener { _, checked ->
            updateTextStyle(editText, checked, defaultColor)
            saveTasks()
        }

        taskContainer.addView(taskView)
    }

    private fun showDeleteConfirmationDialog() {
        val checkedTaskCount = (0 until taskContainer.childCount).count { i ->
            val taskView = taskContainer.getChildAt(i)
            taskView.findViewById<CheckBox>(R.id.checkBox).isChecked
        }

        if (checkedTaskCount == 0) {
            AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("There is no checked task.")
                .setPositiveButton("Okay", null)
                .show()
            return
        }


        AlertDialog.Builder(this)
            .setTitle("Delete Tasks")
            .setMessage("Checked ($checkedTaskCount) task will delete. Are you sure?")
            .setPositiveButton("Yes") { dialog, which ->
                deleteCheckedTasks()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteCheckedTasks() {
        for (i in taskContainer.childCount - 1 downTo 0) {
            val taskView = taskContainer.getChildAt(i)
            val checkBox = taskView.findViewById<CheckBox>(R.id.checkBox)

            if (checkBox.isChecked) {
                taskContainer.removeViewAt(i)
            }
        }
        saveTasks()
    }

    private fun saveTasks() {
        val taskList = ArrayList<TaskData>()

        for (i in 0 until taskContainer.childCount) {
            val taskView = taskContainer.getChildAt(i)

            val editText = taskView.findViewById<EditText>(R.id.editTextTask)
            val checkBox = taskView.findViewById<CheckBox>(R.id.checkBox)

            taskList.add(TaskData(editText.text.toString(), checkBox.isChecked))
        }

        val gson = Gson()
        val jsonString = gson.toJson(taskList)

        val sharedPref = getSharedPreferences("TodoApp", Context.MODE_PRIVATE)
        sharedPref.edit().putString("tasks_json", jsonString).apply()
    }

    private fun loadTasks() {
        val sharedPref = getSharedPreferences("TodoApp", Context.MODE_PRIVATE)
        val jsonString = sharedPref.getString("tasks_json", null)

        if (jsonString != null) {
            val gson = Gson()
            val type = object : TypeToken<ArrayList<TaskData>>() {}.type
            val savedList: ArrayList<TaskData> = gson.fromJson(jsonString, type)

            for (task in savedList) {
                addNewTask(task.text, task.isChecked)
            }
        }
    }

    private fun updateTextStyle(editText: EditText, isChecked: Boolean, defaultColor: Int) {
        if (isChecked) {
            editText.paintFlags = editText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            editText.alpha = 0.5f
            editText.setTextColor(Color.GRAY)
        } else {
            editText.paintFlags = editText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            editText.alpha = 1.0f
            editText.setTextColor(defaultColor)
        }
    }

}