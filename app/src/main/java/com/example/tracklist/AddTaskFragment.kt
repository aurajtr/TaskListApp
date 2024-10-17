package com.example.tracklist

import android.os.Bundle
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
            val title = binding.taskTitleInput.text.toString()
            val description = binding.taskDescriptionInput.text.toString()
            val category = binding.categoryInput.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty() && category.isNotEmpty() && dueDate != null) {
                val task = Task(
                    title = title,
                    description = description,
                    category = category,
                    dueDate = Timestamp(dueDate!!),
                    priority = binding.prioritySlider.value.toInt()
                )
                viewModel.insertTask(task)
                findNavController().navigateUp()
            } else {
                Snackbar.make(view, "Please fill in all fields", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.dueDateButton.setOnClickListener {
            showDatePicker()
        }
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