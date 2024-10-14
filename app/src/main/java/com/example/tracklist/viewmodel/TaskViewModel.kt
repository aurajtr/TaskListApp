package com.example.tracklist.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.tracklist.data.AppDatabase
import com.example.tracklist.data.TaskRepository
import com.example.tracklist.model.Task
import com.example.tracklist.util.PreferencesManager
import com.example.tracklist.util.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    private val preferencesManager: PreferencesManager
    private val filterStatus = MutableLiveData<Boolean?>(null)

    val tasks: LiveData<List<Task>>
    val userPreferences: LiveData<UserPreferences>

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        preferencesManager = PreferencesManager(application)

        tasks = filterStatus.switchMap { status ->
            when (status) {
                null -> repository.allTasks
                else -> repository.getTasksByCompletionStatus(status)
            }
        }

        userPreferences = preferencesManager.preferenceFlow.asLiveData()

        viewModelScope.launch {
            val initialPreferences = preferencesManager.preferenceFlow.first()
            setSortOrder(initialPreferences.sortOrderAscending)
        }
    }

    fun insertTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTask(task)
    }

    fun getTaskById(id: Int): LiveData<Task> {
        return repository.getTaskById(id)
    }

    fun setFilterStatus(status: Boolean?) {
        filterStatus.value = status
    }

    fun setSortOrder(isAscending: Boolean) = viewModelScope.launch {
        preferencesManager.updateSortOrder(isAscending)
    }

    fun setTheme(isDarkTheme: Boolean) = viewModelScope.launch {
        preferencesManager.updateTheme(isDarkTheme)
    }
}