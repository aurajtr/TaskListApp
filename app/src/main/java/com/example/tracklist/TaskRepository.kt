package com.example.tracklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
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
                val taskList = snapshot?.documents?.mapNotNull { it.toObject<Task>() } ?: emptyList()
                _allTasks.value = taskList
            }
    }

    suspend fun insertTask(task: Task) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("tasks").add(task).await()
    }

    suspend fun updateTask(task: Task) {
        val userId = auth.currentUser?.uid ?: return
        task.id?.let {
            firestore.collection("users").document(userId).collection("tasks").document(it).set(task).await()
        }
    }

    suspend fun deleteTask(taskId: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("tasks").document(taskId).delete().await()
    }

    suspend fun getTaskById(taskId: String): Task? {
        val userId = auth.currentUser?.uid ?: return null
        return firestore.collection("users").document(userId).collection("tasks").document(taskId)
            .get().await().toObject<Task>()
    }

    fun getTasksByCategory(category: String): LiveData<List<Task>> {
        val filteredTasks = MutableLiveData<List<Task>>()
        val userId = auth.currentUser?.uid ?: return filteredTasks
        firestore.collection("users").document(userId).collection("tasks")
            .whereEqualTo("category", category)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                val taskList = snapshot?.documents?.mapNotNull { it.toObject<Task>() } ?: emptyList()
                filteredTasks.value = taskList
            }
        return filteredTasks
    }
}