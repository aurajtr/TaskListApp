package com.example.tracklist.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tracklist.model.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY priority DESC, id ASC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskById(id: Int): LiveData<Task>

    @Query("SELECT * FROM tasks WHERE isCompleted = :isCompleted ORDER BY priority DESC, id ASC")
    fun getTasksByCompletionStatus(isCompleted: Boolean): LiveData<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY " +
            "CASE WHEN :isAscending = 1 THEN priority END ASC, " +
            "CASE WHEN :isAscending = 0 THEN priority END DESC, " +
            "id ASC")
    fun getTasksSortedByPriority(isAscending: Boolean): LiveData<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}