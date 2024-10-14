package com.example.tracklist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.tracklist.model.Task
import com.example.tracklist.viewmodel.TaskViewModel
import com.google.android.material.snackbar.Snackbar

class EditTaskActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private var taskId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        val titleEditText: EditText = findViewById(R.id.editTextTitle)
        val descriptionEditText: EditText = findViewById(R.id.editTextDescription)
        val updateButton: Button = findViewById(R.id.buttonUpdate)
        val deleteButton: Button = findViewById(R.id.buttonDelete)

        taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId == -1) {
            finish()
            return
        }

        taskViewModel.getTaskById(taskId).observe(this) { task ->
            task?.let {
                titleEditText.setText(it.title)
                descriptionEditText.setText(it.description)
            }
        }

        updateButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            if (title.isNotEmpty()) {
                val updatedTask = Task(id = taskId, title = title, description = description)
                taskViewModel.updateTask(updatedTask)
                finish()
            } else {
                Snackbar.make(it, "Title cannot be empty", Snackbar.LENGTH_SHORT).show()
            }
        }

        deleteButton.setOnClickListener {
            taskViewModel.deleteTask(Task(id = taskId, title = "", description = ""))
            finish()
        }
    }
}