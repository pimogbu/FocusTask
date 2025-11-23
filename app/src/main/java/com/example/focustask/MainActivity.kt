package com.example.focustask

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val checkBox = findViewById<CheckBox>(R.id.checkBox)
        val editText = findViewById<EditText>(R.id.editTextTask)

        val defaultColor = editText.currentTextColor

        checkBox.setOnCheckedChangeListener { _, isChecked ->
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
}