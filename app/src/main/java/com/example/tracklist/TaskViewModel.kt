package com.example.tracklist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    val allTasks: LiveData<List<Task>>

    private val _filteredTasks = MutableLiveData<List<Task>>()
    val filteredTasks: LiveData<List<Task>> = _filteredTasks

    private var currentFilter: String? = null
    private var currentSortOrder: SortOrder = SortOrder.DATE

    init {
        if (FirebaseApp.getApps(application).isEmpty()) {
            FirebaseApp.initializeApp(application)
        }
        repository = TaskRepository()
        allTasks = repository.allTasks
        _filteredTasks.value = allTasks.value
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
        applyFilterAndSort()
    }

    fun deleteTask(taskId: String) = viewModelScope.launch {
        repository.deleteTask(taskId)
        applyFilterAndSort()
    }

    fun getTaskById(taskId: String): LiveData<Task?> {
        val result = MutableLiveData<Task?>()
        viewModelScope.launch {
            val task = allTasks.value?.find { it.id == taskId }
            result.postValue(task)
        }
        return result
    }

    fun filterTasks(category: String?) {
        currentFilter = category
        applyFilterAndSort()
    }

    fun sortTasks(sortOrder: SortOrder) {
        currentSortOrder = sortOrder
        applyFilterAndSort()
    }

    private fun applyFilterAndSort() {
        viewModelScope.launch {
            var filteredList = allTasks.value ?: emptyList()

            // Apply filter
            currentFilter?.let { filter ->
                filteredList = filteredList.filter { it.category == filter }
            }

            // Apply sort
            filteredList = when (currentSortOrder) {
                SortOrder.DATE -> filteredList.sortedBy { it.dueDate }
                SortOrder.PRIORITY -> filteredList.sortedByDescending { it.priority }
                SortOrder.ALPHABETICAL -> filteredList.sortedBy { it.title }
            }

            _filteredTasks.postValue(filteredList)
        }
    }

    enum class SortOrder {
        DATE, PRIORITY, ALPHABETICAL
    }
}