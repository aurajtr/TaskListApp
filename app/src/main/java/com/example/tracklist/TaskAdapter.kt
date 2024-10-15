package com.example.tracklist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tracklist.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onItemClick: (Task) -> Unit,
    private val onItemLongClick: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.apply {
                taskTitle.text = task.title
                taskDescription.text = task.description
                taskPriority.text = "Priority: ${task.priority}"
                taskDueDate.text = "Due: ${dateFormat.format(task.dueDate)}"

                // Color code priority
                val priorityColor = when (task.priority) {
                    1 -> Color.GREEN
                    2 -> Color.YELLOW
                    else -> Color.RED
                }
                taskPriority.setTextColor(priorityColor)

                // Set content description for accessibility
                root.contentDescription = buildContentDescription(task)

                root.setOnClickListener { onItemClick(task) }
                root.setOnLongClickListener {
                    onItemLongClick(task)
                    true
                }
            }
        }

        private fun buildContentDescription(task: Task): String {
            return "Task: ${task.title}. " +
                    "Description: ${task.description}. " +
                    "Priority: ${task.priority}. " +
                    "Due: ${dateFormat.format(task.dueDate)}. " +
                    "Double tap to edit, long press to delete."
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}