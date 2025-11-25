package com.example.focustask

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FolderSelectionActivity : AppCompatActivity() {

    private lateinit var folderContainer: LinearLayout
    private val folderList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_selection)

        folderContainer = findViewById(R.id.folderContainer)
        val buttonAddFolder = findViewById<Button>(R.id.buttonAddFolder)

        loadFolders()

        buttonAddFolder.setOnClickListener {
            showAddFolderDialog()
        }
    }

    private fun showAddFolderDialog() {
        val input = EditText(this)
        input.hint = "Folder Name"

        AlertDialog.Builder(this)
            .setTitle("Create New Folder")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val folderName = input.text.toString().trim()
                if (folderName.isNotEmpty() && !folderList.contains(folderName)) {

                    addFolderView(folderName)
                    folderList.add(folderName)
                    saveFolders()

                    createDefaultTaskForFolder(folderName)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun createDefaultTaskForFolder(folderName: String) {
        val defaultTask = TaskData("", false)
        val initialList = arrayListOf(defaultTask)

        val gson = Gson()
        val jsonString = gson.toJson(initialList)

        val prefName = "Tasks_$folderName"
        getSharedPreferences(prefName, Context.MODE_PRIVATE)
            .edit()
            .putString("tasks_json", jsonString)
            .apply()
    }

    private fun addFolderView(name: String) {
        val folderView = layoutInflater.inflate(R.layout.item_folder, null)
        val textName = folderView.findViewById<TextView>(R.id.textViewFolderName)
        val btnDelete = folderView.findViewById<ImageButton>(R.id.buttonDeleteFolder)

        textName.text = name

        folderView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("FOLDER_NAME", name)
            startActivity(intent)
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Folder")
                .setMessage("Delete '$name' and all its tasks?")
                .setPositiveButton("Yes") { _, _ ->
                    folderContainer.removeView(folderView)
                    folderList.remove(name)
                    saveFolders()

                    getSharedPreferences("Tasks_$name", Context.MODE_PRIVATE).edit().clear().apply()
                }
                .setNegativeButton("No", null)
                .show()
        }

        folderContainer.addView(folderView)
    }

    private fun saveFolders() {
        val gson = Gson()
        val jsonString = gson.toJson(folderList)
        getSharedPreferences("AppFolders", Context.MODE_PRIVATE)
            .edit().putString("folder_list", jsonString).apply()
    }

    private fun loadFolders() {
        val jsonString = getSharedPreferences("AppFolders", Context.MODE_PRIVATE)
            .getString("folder_list", null)

        if (jsonString != null) {
            val type = object : TypeToken<ArrayList<String>>() {}.type
            val savedList: ArrayList<String> = Gson().fromJson(jsonString, type)
            folderList.clear()
            folderList.addAll(savedList)

            for (folderName in folderList) {
                addFolderView(folderName)
            }
        }
    }
}