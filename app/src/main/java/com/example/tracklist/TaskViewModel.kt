package com.example.tracklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()
    private val _sortedTasks = MutableLiveData<List<Task>>()
    val sortedTasks: LiveData<List<Task>> = _sortedTasks

    private var currentSortOrder: SortOrder = SortOrder.DATE

    init {
        repository.allTasks.observeForever { tasks ->
            sortTasks(currentSortOrder, tasks)
        }
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
    }

    fun deleteTask(taskId: String) = viewModelScope.launch {
        repository.deleteTask(taskId)
    }

    fun getTaskById(taskId: String): LiveData<Task?> {
        return repository.getTaskById(taskId)
    }

    fun sortTasks(sortOrder: SortOrder) {
        currentSortOrder = sortOrder
        sortTasks(sortOrder, repository.allTasks.value ?: emptyList())
    }

    private fun sortTasks(sortOrder: SortOrder, tasks: List<Task>) {
        val sortedList = when (sortOrder) {
            SortOrder.DATE -> tasks.sortedBy { it.dueDate }
            SortOrder.PRIORITY -> tasks.sortedByDescending { it.priority }
            SortOrder.ALPHABETICAL -> tasks.sortedBy { it.title }
        }
        _sortedTasks.value = sortedList
    }

    enum class SortOrder {
        DATE, PRIORITY, ALPHABETICAL
    }
}