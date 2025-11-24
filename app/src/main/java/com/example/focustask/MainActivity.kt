package com.example.focustask

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
val Context.dataStore by preferencesDataStore(name = "todo_settings")

class MainActivity : AppCompatActivity() {
    private lateinit var checkBox: CheckBox
    private lateinit var editText: EditText

    private val TASK_TEXT_KEY = stringPreferencesKey("task_text")
    private val IS_CHECKED_KEY = booleanPreferencesKey("is_checked")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkBox = findViewById(R.id.checkBox)
        editText = findViewById(R.id.editTextTask)
        val defaultColor = editText.currentTextColor

        lifecycleScope.launch {
            val preferences = dataStore.data.first()

            val savedText = preferences[TASK_TEXT_KEY] ?: ""
            val savedChecked = preferences[IS_CHECKED_KEY] ?: false

            editText.setText(savedText)
            checkBox.isChecked = savedChecked

            updateTextStyle(savedChecked, defaultColor)
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            updateTextStyle(isChecked, defaultColor)
        }
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch(Dispatchers.IO) {
            val currentText = editText.text.toString()
            val currentChecked = checkBox.isChecked

            dataStore.edit { settings ->
                settings[TASK_TEXT_KEY] = currentText
                settings[IS_CHECKED_KEY] = currentChecked
            }
        }
    }

    private fun updateTextStyle(isChecked: Boolean, defaultColor: Int) {
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