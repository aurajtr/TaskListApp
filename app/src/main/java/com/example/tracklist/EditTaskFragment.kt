package com.example.tracklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tracklist.databinding.FragmentEditTaskBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import java.util.*

class EditTaskFragment : Fragment() {
    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels()
    private val args: EditTaskFragmentArgs by navArgs()
    private var dueDate: Date? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getTaskById(args.taskId).observe(viewLifecycleOwner) { task ->
            task?.let {
                binding.taskTitleInput.setText(it.title)
                binding.taskDescriptionInput.setText(it.description)
                binding.categoryInput.setText(it.category)
                binding.prioritySlider.value = it.priority.toFloat()
                dueDate = it.dueDate?.toDate()
                binding.dueDateButton.text = "Due: ${dueDate?.toString() ?: "Not set"}"
            }
        }

        binding.saveChangesButton.setOnClickListener {
            updateTask()}

        binding.deleteTaskButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.dueDateButton.setOnClickListener {
            showDatePicker()
        }
    }

    private fun updateTask() {
        val title = binding.taskTitleInput.text.toString()
        val description = binding.taskDescriptionInput.text.toString()
        val category = binding.categoryInput.text.toString()

        if (title.isNotEmpty() && description.isNotEmpty() && category.isNotEmpty() && dueDate != null) {
            val updatedTask = Task(
                id = args.taskId,
                title = title,
                description = description,
                category = category,
                dueDate = Timestamp(dueDate!!),
                priority = binding.prioritySlider.value.toInt()
            )
            viewModel.updateTask(updatedTask)
            findNavController().navigateUp()
        } else {
            Snackbar.make(requireView(), "Please fill in all fields", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteTask(args.taskId)
                findNavController().navigateUp()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select due date")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            dueDate = Date(selection)
            binding.dueDateButton.text = "Due: ${dueDate?.toString() ?: "Not set"}"
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}