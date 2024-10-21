package com.example.tracklist.test

import android.content.Context
import androidx.room.Room
import com.example.tracklist.Task

class RoomTaskRepository(context: Context) : TaskRepository {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "task_database"
    ).build()

    private val taskDao = database.taskDao()

    override suspend fun insertTask(task: Task): Boolean {
        return try {
            taskDao.insertTask(task)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getAllTasks(): List<Task> {
        return try {
            taskDao.getAllTasks()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun updateTask(task: Task): Boolean {
        return try {
            taskDao.updateTask(task)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteTask(taskId: String): Boolean {
        return try {
            taskDao.deleteTask(taskId)
            true
        } catch (e: Exception) {
            false
        }
    }
}