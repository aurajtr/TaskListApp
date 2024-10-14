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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}