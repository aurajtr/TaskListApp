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

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        val titleEditText: EditText = findViewById(R.id.editTextTitle)
        val descriptionEditText: EditText = findViewById(R.id.editTextDescription)
        val priorityRadioGroup: RadioGroup = findViewById(R.id.radioGroupPriority)
        val saveButton: Button = findViewById(R.id.buttonSave)

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val priority = when (priorityRadioGroup.checkedRadioButtonId) {
                R.id.radioButtonLow -> 0
                R.id.radioButtonMedium -> 1
                R.id.radioButtonHigh -> 2
                else -> 0
            }

            val newTask = Task(title = title, description = description, priority = priority)

            when (val validationResult = TaskValidator.validateTask(newTask)) {
                is ValidationResult.Success -> {
                    taskViewModel.insertTask(newTask)
                    finish()
                }
                is ValidationResult.Error -> {
                    Snackbar.make(it, validationResult.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}