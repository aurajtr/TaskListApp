package com.example.tracklist

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: String): LiveData<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    @Query("SELECT * FROM tasks WHERE category = :category")
    fun getTasksByCategory(category: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getTasksSortedByDate(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY priority DESC")
    fun getTasksSortedByPriority(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY title ASC")
    fun getTasksSortedAlphabetically(): LiveData<List<Task>>
}