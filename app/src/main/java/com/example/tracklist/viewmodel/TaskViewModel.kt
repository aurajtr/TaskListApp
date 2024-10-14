package com.example.tracklist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.tracklist.data.AppDatabase
import com.example.tracklist.data.TaskRepository
import com.example.tracklist.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    private val filterStatus = MutableLiveData<Boolean?>(null)
    private val sortOrder = MutableLiveData<Boolean>(false)

    val tasks: LiveData<List<Task>> = filterStatus.switchMap { status ->
        when (status) {
            null -> repository.allTasks
            else -> repository.getTasksByCompletionStatus(status)
        }
    }

    val sortedTasks: LiveData<List<Task>> = sortOrder.switchMap { isAscending ->
        repository.getTasksSortedByPriority(isAscending)
    }

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
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

    fun setSortOrder(isAscending: Boolean) {
        sortOrder.value = isAscending
    }
}