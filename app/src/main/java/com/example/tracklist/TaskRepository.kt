package com.example.tracklist

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

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("tasks")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                val taskList = snapshot?.documents?.mapNotNull { it.toObject(Task::class.java) } ?: emptyList()
                _allTasks.value = taskList
            }
    }

    suspend fun insertTask(task: Task) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("tasks").add(task).await()
    }

    suspend fun updateTask(task: Task) {
        val userId = auth.currentUser?.uid ?: return
        task.id.let {
            firestore.collection("users").document(userId).collection("tasks").document(it).set(task).await()
        }
    }

    suspend fun deleteTask(taskId: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("tasks").document(taskId).delete().await()
    }

    fun getTaskById(taskId: String): LiveData<Task?> {
        val result = MutableLiveData<Task?>()
        val userId = auth.currentUser?.uid ?: return result
        firestore.collection("users").document(userId).collection("tasks").document(taskId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                result.value = snapshot?.toObject(Task::class.java)
            }
        return result
    }
}