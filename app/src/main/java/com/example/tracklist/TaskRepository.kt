package com.example.tracklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class TaskRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val tasksCollection = firestore.collection("tasks")
    private val _allTasks = MutableLiveData<List<Task>>()
    val allTasks: LiveData<List<Task>> = _allTasks

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        tasksCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle error
                return@addSnapshotListener
            }
            val taskList = snapshot?.documents?.mapNotNull { it.toObject<Task>() } ?: emptyList()
            _allTasks.value = taskList
        }
    }

    suspend fun insertTask(task: Task) {
        tasksCollection.add(task)
    }

    suspend fun updateTask(task: Task) {
        task.id?.let { tasksCollection.document(it).set(task) }
    }

    suspend fun deleteTask(taskId: String) {
        tasksCollection.document(taskId).delete()
    }
}