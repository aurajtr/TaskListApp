package com.example.tracklist.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.tracklist.data.AppDatabase
import com.example.tracklist.data.TaskRepository
import com.example.tracklist.model.Task
import com.example.tracklist.util.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    private val preferencesManager: PreferencesManager
    private val filterStatus = MutableLiveData<Boolean?>(null)
    private val searchQuery = MutableLiveData<String>("")

    val tasks: LiveData<List<Task>>
    val userPreferences: LiveData<PreferencesManager.UserPreferences>

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        preferencesManager = PreferencesManager(application)

        tasks = MediatorLiveData<List<Task>>().apply {
            addSource(filterStatus) { updateTasks() }
            addSource(searchQuery) { updateTasks() }
        }

        userPreferences = preferencesManager.preferenceFlow.asLiveData()

        viewModelScope.launch {
            val initialPreferences = preferencesManager.preferenceFlow.first()
            setSortOrder(initialPreferences.sortOrderAscending)
        }
    }

    private fun updateTasks() {
        val currentFilterStatus = filterStatus.value
        val currentSearchQuery = searchQuery.value ?: ""

        tasks.value = when {
            currentFilterStatus != null -> repository.getTasksByCompletionStatus(currentFilterStatus).value
            currentSearchQuery.isNotEmpty() -> repository.searchTasks(currentSearchQuery).value
            else -> repository.allTasks.value
        }?.filter { task ->
            (currentFilterStatus == null || task.isCompleted == currentFilterStatus) &&
                    (currentSearchQuery.isEmpty() || task.title.contains(currentSearchQuery, ignoreCase = true) ||
                            task.description.contains(currentSearchQuery, ignoreCase = true))
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

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setSortOrder(isAscending: Boolean) = viewModelScope.launch {
        preferencesManager.updateSortOrder(isAscending)
    }

    fun setTheme(isDarkTheme: Boolean) = viewModelScope.launch {
        preferencesManager.updateTheme(isDarkTheme)
    }
}