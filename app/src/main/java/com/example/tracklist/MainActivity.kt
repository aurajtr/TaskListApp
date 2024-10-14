package com.example.tracklist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tracklist.adapter.TaskAdapter
import com.example.tracklist.model.Task
import com.example.tracklist.viewmodel.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        taskAdapter = TaskAdapter(this) { task -> onTaskClick(task) }
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        taskViewModel.allTasks.observe(this) { tasks ->
            taskAdapter.submitList(tasks)
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onTaskClick(task: Task) {
        // TODO: Implement task click functionality
    }
}