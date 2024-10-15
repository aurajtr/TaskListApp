package com.example.tracklist

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.tracklist.databinding.DialogAddEditTaskBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class AddEditTaskDialogFragment : DialogFragment() {

    private lateinit var binding: DialogAddEditTaskBinding
    private lateinit var viewModel: TaskViewModel
    private var taskId: Int = -1
    private var dueDate: Date = Date()

    companion object {
        private const val ARG_TASK_ID = "task_id"

        fun newInstance(taskId: Int): AddEditTaskDialogFragment {
            val fragment = AddEditTaskDialogFragment()
            val args = Bundle()
            args.putInt(ARG_TASK_ID, taskId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskId = arguments?.getInt(ARG_TASK_ID, -1) ?: -1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogAddEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(TaskViewModel::class.java)

        setupDatePicker()
        setupSaveButton()

        if (taskId != -1) {
            loadTaskData()
        }
    }

    private fun setupDatePicker() {
        binding.taskDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    dueDate = calendar.time
                    updateDateDisplay()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        updateDateDisplay()
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        binding.taskDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    binding.taskDueDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            val title = binding.taskTitle.text.toString()
            val description = binding.taskDescription.text.toString()
            val priority = binding.taskPriority.text.toString().toIntOrNull() ?: 1

            if (title.isBlank()) {
                Snackbar.make(binding.root, "Title cannot be empty", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val task = Task(
                id = if (taskId != -1) taskId else 0,
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate
            )

            if (taskId != -1) {
                viewModel.update(task)
            } else {
                viewModel.insert(task)
            }

            dismiss()
        }
    }

    private fun loadTaskData() {
        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            val task = tasks.find { it.id == taskId }
            task?.let {
                binding.taskTitle.setText(it.title)
                binding.taskDescription.setText(it.description)
                binding.taskPriority.setText(it.priority.toString())
                dueDate = it.dueDate
                updateDateDisplay()
            }
        }
    }
}