package com.example.tracklist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    val allTasks: LiveData<List<Task>>

    init {
        if (FirebaseApp.getApps(application).isEmpty()) {
            FirebaseApp.initializeApp(application)
        }
        repository = TaskRepository()
        allTasks = repository.allTasks
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
}