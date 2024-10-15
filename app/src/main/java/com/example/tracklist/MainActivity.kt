package com.example.tracklist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.tracklist.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TaskViewModel
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        setupFab()
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            AddEditTaskDialogFragment().show(supportFragmentManager, "AddEditTaskDialog")
        }
    }

    fun editTask(task: Task) {
        AddEditTaskDialogFragment.newInstance(task.id)
            .show(supportFragmentManager, "AddEditTaskDialog")
    }

    fun deleteTask(task: Task) {
        viewModel.delete(task)
        Snackbar.make(binding.root, "Task deleted", Snackbar.LENGTH_LONG)
            .setAction("UNDO") { viewModel.insert(task) }
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_date -> {
                viewModel.sortByDate()
                true
            }
            R.id.action_sort_by_priority -> {
                viewModel.sortByPriority()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}