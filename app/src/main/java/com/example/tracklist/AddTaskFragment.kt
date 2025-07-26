package com.example.tracklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tracklist.databinding.FragmentAddTaskBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import java.util.*

class AddTaskFragment : Fragment() {
    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels()
    private var dueDate: Date? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addTaskButton.setOnClickListener {
            addTask()
        }

        binding.dueDateButton.setOnClickListener {
            showDatePicker()
        }
    }

    private fun addTask() {
        val title = binding.taskTitleInput.text.toString().trim()
        val description = binding.taskDescriptionInput.text.toString().trim()
        val category = binding.categoryInput.text.toString().trim()

        // Clear previous errors
        binding.taskTitleInputLayout.error = null
        binding.taskDescriptionInputLayout.error = null
        binding.categoryInputLayout.error = null

        // Validate inputs
        var isValid = true

        if (title.isEmpty()) {
            binding.taskTitleInputLayout.error = "Title is required"
            isValid = false
        }

        if (description.isEmpty()) {
            binding.taskDescriptionInputLayout.error = "Description is required"
            isValid = false
        }

        if (category.isEmpty()) {
            binding.categoryInputLayout.error = "Category is required"
            isValid = false
        }

        if (dueDate == null) {
            Snackbar.make(binding.root, "Please select a due date", Snackbar.LENGTH_SHORT).show()
            isValid = false
        }

        if (!isValid) return

        // Show loading
        showLoading(true)

        try {
            val task = Task(
                title = title,
                description = description,
                category = category,
                dueDate = Timestamp(dueDate!!),
                priority = binding.prioritySlider.value.toInt(),
                isCompleted = false
            )

            Log.d("AddTaskFragment", "Creating task: $task")

            viewModel.insertTask(task)

            // Show success message and navigate back
            Snackbar.make(binding.root, "Task added successfully!", Snackbar.LENGTH_SHORT).show()

            // Navigate back after a short delay
            binding.root.postDelayed({
                if (isAdded) {
                    findNavController().navigateUp()
                }
            }, 1000)

        } catch (e: Exception) {
            Log.e("AddTaskFragment", "Error adding task", e)
            showLoading(false)
            Snackbar.make(binding.root, "Error adding task: ${e.message}", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select due date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            dueDate = Date(selection)
            binding.dueDateButton.text = "Due: ${java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(dueDate!!)}"
            Log.d("AddTaskFragment", "Date selected: $dueDate")
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun showLoading(isLoading: Boolean) {
        binding.addTaskButton.isEnabled = !isLoading
        binding.addTaskButton.text = if (isLoading) "Adding..." else "Add Task"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}