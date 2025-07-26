package com.example.tracklist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()
    private val _sortedTasks = MutableLiveData<List<Task>>()
    val sortedTasks: LiveData<List<Task>> = _sortedTasks

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var currentSortOrder: SortOrder = SortOrder.DATE

    companion object {
        private const val TAG = "TaskViewModel"
    }

    init {
        repository.allTasks.observeForever { tasks ->
            Log.d(TAG, "Tasks updated in ViewModel: ${tasks?.size ?: 0}")
            sortTasks(currentSortOrder, tasks ?: emptyList())
        }
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        try {
            _isLoading.value = true
            _errorMessage.value = null

            Log.d(TAG, "Inserting task: $task")
            val result = repository.insertTask(task)

            if (result.isSuccess) {
                Log.d(TAG, "Task inserted successfully with ID: ${result.getOrNull()}")
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Log.e(TAG, "Failed to insert task: $error")
                _errorMessage.value = "Failed to add task: $error"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception inserting task", e)
            _errorMessage.value = "Error adding task: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        try {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.updateTask(task)

            if (result.isSuccess) {
                Log.d(TAG, "Task updated successfully")
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Log.e(TAG, "Failed to update task: $error")
                _errorMessage.value = "Failed to update task: $error"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception updating task", e)
            _errorMessage.value = "Error updating task: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun deleteTask(taskId: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.deleteTask(taskId)

            if (result.isSuccess) {
                Log.d(TAG, "Task deleted successfully")
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Log.e(TAG, "Failed to delete task: $error")
                _errorMessage.value = "Failed to delete task: $error"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception deleting task", e)
            _errorMessage.value = "Error deleting task: ${e.message}"
        } finally {
            _isLoading.value = false
        }
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
            SortOrder.ALPHABETICAL -> tasks.sortedBy { it.title.lowercase() }
        }
        _sortedTasks.value = sortedList
        Log.d(TAG, "Tasks sorted by $sortOrder: ${sortedList.size} items")
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    enum class SortOrder {
        DATE, PRIORITY, ALPHABETICAL
    }
}