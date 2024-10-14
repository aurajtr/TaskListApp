package com.example.tracklist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.tracklist.model.Task
import com.example.tracklist.util.TaskValidator
import com.example.tracklist.util.ValidationResult
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
        val priorityRadioGroup: RadioGroup = findViewById(R.id.radioGroupPriority)
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
                when (it.priority) {
                    0 -> priorityRadioGroup.check(R.id.radioButtonLow)
                    1 -> priorityRadioGroup.check(R.id.radioButtonMedium)
                    2 -> priorityRadioGroup.check(R.id.radioButtonHigh)
                }
            }
        }

        updateButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val priority = when (priorityRadioGroup.checkedRadioButtonId) {
                R.id.radioButtonLow -> 0
                R.id.radioButtonMedium -> 1
                R.id.radioButtonHigh -> 2
                else -> 0
            }

            val updatedTask = Task(id = taskId, title = title, description = description, priority = priority)

            when (val validationResult = TaskValidator.validateTask(updatedTask)) {
                is ValidationResult.Success -> {
                    taskViewModel.updateTask(updatedTask)
                    finish()
                }
                is ValidationResult.Error -> {
                    Snackbar.make(it, validationResult.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        deleteButton.setOnClickListener {
            taskViewModel.deleteTask(Task(id = taskId, title = "", description = ""))
            finish()
        }
    }
}