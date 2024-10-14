package com.example.tracklist.data

import androidx.lifecycle.LiveData
import com.example.tracklist.model.Task

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    fun getTasksByCompletionStatus(isCompleted: Boolean): LiveData<List<Task>> {
        return taskDao.getTasksByCompletionStatus(isCompleted)
    }

    fun getTasksSortedByPriority(isAscending: Boolean): LiveData<List<Task>> {
        return taskDao.getTasksSortedByPriority(isAscending)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    fun getTaskById(id: Int): LiveData<Task> {
        return taskDao.getTaskById(id)
    }
}