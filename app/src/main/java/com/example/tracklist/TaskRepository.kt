package com.example.tracklist

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    fun sortByDate() {
        // The sorting is already done in the DAO query
    }

    fun sortByPriority(): LiveData<List<Task>> {
        return taskDao.getTasksSortedByPriority()
    }
}