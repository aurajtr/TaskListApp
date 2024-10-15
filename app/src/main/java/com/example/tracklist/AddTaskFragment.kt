package com.example.tracklist

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tracklist.databinding.FragmentAddTaskBinding
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
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

        binding.dueDateButton.setOnClickListener {
            showDatePicker()
        }

        binding.addTaskButton.setOnClickListener {
            val title = binding.taskTitleInput.text.toString()
            val description = binding.taskDescriptionInput.text.toString()
            val category = binding.categoryInput.text.toString()

            if (title.isNotBlank()) {
                val task = Task(
                    title = title,
                    description = description,
                    category = category,
                    dueDate = dueDate?.let { Timestamp(it) }
                )
                viewModel.insertTask(task)
                findNavController().navigateUp()
            } else {
                Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                dueDate = calendar.time
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                binding.dueDateButton.text = "Due: ${dateFormat.format(calendar.time)}"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}