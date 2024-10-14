package com.example.tracklist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
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
        taskViewModel.tasks.observe(this) { tasks ->
            taskAdapter.submitList(tasks)
        }

        taskViewModel.userPreferences.observe(this) { preferences ->
            AppCompatDelegate.setDefaultNightMode(
                if (preferences.isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            taskViewModel.setSortOrder(preferences.sortOrderAscending)
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                taskViewModel.setSearchQuery(newText ?: "")
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_filter_all -> {
                taskViewModel.setFilterStatus(null)
                true
            }
            R.id.menu_filter_active -> {
                taskViewModel.setFilterStatus(false)
                true
            }
            R.id.menu_filter_completed -> {
                taskViewModel.setFilterStatus(true)
                true
            }
            R.id.menu_sort_priority_asc -> {
                taskViewModel.setSortOrder(true)
                true
            }
            R.id.menu_sort_priority_desc -> {
                taskViewModel.setSortOrder(false)
                true
            }
            R.id.menu_toggle_theme -> {
                taskViewModel.setTheme(!AppCompatDelegate.getDefaultNightMode().equals(AppCompatDelegate.MODE_NIGHT_YES))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onTaskClick(task: Task) {
        val intent = Intent(this, EditTaskActivity::class.java)
        intent.putExtra("TASK_ID", task.id)
        startActivity(intent)
    }
}