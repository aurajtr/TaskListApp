package com.example.tracklist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TaskRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _allTasks = MutableLiveData<List<Task>>()
    val allTasks: LiveData<List<Task>> = _allTasks

    companion object {
        private const val TAG = "TaskRepository"
    }

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "No user logged in")
            _allTasks.value = emptyList()
            return
        }

        Log.d(TAG, "Fetching tasks for user: $userId")

        firestore.collection("users").document(userId).collection("tasks")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e)
                    _allTasks.value = emptyList()
                    return@addSnapshotListener
                }

                val taskList = snapshot?.documents?.mapNotNull { document ->
                    try {
                        val task = document.toObject(Task::class.java)
                        task?.copy(id = document.id)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting document to task", e)
                        null
                    }
                } ?: emptyList()

                Log.d(TAG, "Fetched ${taskList.size} tasks")
                _allTasks.value = taskList
            }
    }

    suspend fun insertTask(task: Task): Result<String> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                Log.e(TAG, "No user logged in for insert")
                return Result.failure(Exception("User not authenticated"))
            }

            Log.d(TAG, "Inserting task for user: $userId")
            Log.d(TAG, "Task data: $task")

            // Use .add() to auto-generate ID
            val docRef = firestore.collection("users")
                .document(userId)
                .collection("tasks")
                .add(task)
                .await()

            Log.d(TAG, "Task inserted with ID: ${docRef.id}")
            Result.success(docRef.id)

        } catch (e: Exception) {
            Log.e(TAG, "Error inserting task", e)
            Result.failure(e)
        }
    }

    suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                return Result.failure(Exception("User not authenticated"))
            }

            if (task.id.isEmpty()) {
                return Result.failure(Exception("Task ID is empty"))
            }

            firestore.collection("users")
                .document(userId)
                .collection("tasks")
                .document(task.id)
                .set(task)
                .await()

            Log.d(TAG, "Task updated: ${task.id}")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error updating task", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                return Result.failure(Exception("User not authenticated"))
            }

            firestore.collection("users")
                .document(userId)
                .collection("tasks")
                .document(taskId)
                .delete()
                .await()

            Log.d(TAG, "Task deleted: $taskId")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error deleting task", e)
            Result.failure(e)
        }
    }

    fun getTaskById(taskId: String): LiveData<Task?> {
        val result = MutableLiveData<Task?>()
        val userId = auth.currentUser?.uid

        if (userId == null) {
            result.value = null
            return result
        }

        firestore.collection("users")
            .document(userId)
            .collection("tasks")
            .document(taskId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Error getting task by ID", e)
                    result.value = null
                    return@addSnapshotListener
                }

                val task = snapshot?.toObject(Task::class.java)
                result.value = task?.copy(id = snapshot.id)
            }
        return result
    }
}