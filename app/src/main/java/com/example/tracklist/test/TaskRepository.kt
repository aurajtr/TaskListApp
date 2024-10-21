package com.example.tracklist.test

import com.example.tracklist.Task

interface TaskRepository {
    suspend fun insertTask(task: Task): Boolean
    suspend fun getAllTasks(): List<Task>
    suspend fun updateTask(task: Task): Boolean
    suspend fun deleteTask(taskId: String): Boolean
}