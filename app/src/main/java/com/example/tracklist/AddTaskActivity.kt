package com.example.tracklist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.tracklist.model.Task
import com.example.tracklist.viewmodel.TaskViewModel
import com.google.android.material.snackbar.Snackbar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        val titleEditText: EditText = findViewById(R.id.editTextTitle)
        val descriptionEditText: EditText = findViewById(R.id.editTextDescription)
        val saveButton: Button = findViewById(R.id.buttonSave)

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            if (title.isNotEmpty()) {
                val newTask = Task(title = title, description = description)
                taskViewModel.insertTask(newTask)
                finish()
            } else {
                Snackbar.make(it, "Title cannot be empty", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}