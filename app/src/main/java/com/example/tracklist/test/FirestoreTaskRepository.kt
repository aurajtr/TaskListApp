package com.example.tracklist.test

import com.example.tracklist.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreTaskRepository : TaskRepository {
    private val db = FirebaseFirestore.getInstance()
    private val tasksCollection = db.collection("tasks")

    override suspend fun insertTask(task: Task): Boolean {
        return try {
            tasksCollection.add(task).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getAllTasks(): List<Task> {
        return try {
            tasksCollection.get().await().toObjects(Task::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun updateTask(task: Task): Boolean {
        return try {
            tasksCollection.document(task.id).set(task).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteTask(taskId: String): Boolean {
        return try {
            tasksCollection.document(taskId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}